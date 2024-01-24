package github.visual4.aacweb.dictation.domain.order.gbuying;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.Util;
import github.visual4.aacweb.dictation.domain.order.GroupOrderForms;
import github.visual4.aacweb.dictation.domain.order.Order;
import github.visual4.aacweb.dictation.domain.order.OrderService;
import github.visual4.aacweb.dictation.domain.order.group.GroupOrderDao;
import github.visual4.aacweb.dictation.domain.order.group.GroupOrderForm;
import github.visual4.aacweb.dictation.domain.order.group.GroupOrderService;
import github.visual4.aacweb.dictation.domain.product.Product;
import github.visual4.aacweb.dictation.domain.product.ProductService;
import github.visual4.aacweb.dictation.domain.product.Products;
import github.visual4.aacweb.dictation.domain.user.User;
import github.visual4.aacweb.dictation.domain.user.UserService;
import github.visual4.aacweb.dictation.service.mailing.MailDto;
import github.visual4.aacweb.dictation.service.mailing.MailingService;
/**
 * 공동구매 관련 기능들. 공구 상품 추가/삭제, 참여자 등록 및 실제 주문 생성
 */
@Service
@Transactional
public class GbuyingOrderService {
	final String adminEmail;
	final UserService userService;
	final ProductService productService;
	final GroupOrderService groupOrderService;	
	final MailingService mailingService;
	final ObjectMapper om;
	
	public GbuyingOrderService(
			@Value("${dictation.ncp.mailing.receiver}") String receiverMail,
			UserService userService, 
			ProductService productService,
			GroupOrderService groupOrderService,
			MailingService mailingService,
			ObjectMapper om ) {
		this.adminEmail = receiverMail;
		this.userService = userService;
		this.productService = productService;
		this.groupOrderService = groupOrderService;
		this.mailingService = mailingService;
		this.om = om;
	}
	/**
	 * 접수된 주문 양식 조회(발생한 주문 정보가 아님)
	 * @param productUuid
	 */
	public List<GroupOrderForm> findOderForms(String productUuid) {
		Product product = productService.findBy(Product.Column.prod_code, productUuid);
		checkIfGroupBuyingProduct(product);
		
		List<GroupOrderForm> forms = groupOrderService.findOrderForms(
				GroupOrderForm.Column.target_product_ref,
				product.getSeq());
		forms.forEach(GroupOrderForms::unescape);
		return forms;
	}
	
	private void checkIfGroupBuyingProduct(Product product) {
		if (product == null) {
			throw new AppException(ErrorCode.PRODUCT_ERROR, 400, "no such product");
		}
		if (!product.checkIfGroupBuying()) {
			throw new AppException(ErrorCode.PRODUCT_ERROR, 400, "not a GroupBuying Product");
		}
	}
	/**
	 * 공동구매 문의 등록(실제 주문이 아님)
	 * @param admin
	 * @param form
	 * @return 
	 */
	public GroupOrderForm insertOrderForm(GroupOrderForm form) {
		TypeMap content = Util.parseJson(om, form.getContent());
		/* content (price: string, qtt: string, userRef: number) */
		
		Integer userRef = content.asInt("userRef");
		User user = userService.findTeacher(userRef.longValue());
		
		Product product = productService.findBy(Product.Column.prod_seq, form.getProductRef());
		Products.checkIfGBuyingProduct(product);
		
		List<GroupOrderForm> forms = groupOrderService.findOrderForms(
				GroupOrderForm.Column.target_product_ref,
				product.getSeq());
		
		GroupOrderForm dup = forms.stream()
				.filter(item -> item.checkIfOwner(user))
				.findAny().orElse(null);
		if (dup != null) {
			throw new AppException(ErrorCode.GROUP_ORDER_ERROR, 422, "user_aleady_exist");
		}
		form.setOrgName("");
		form.setOrgEmail("");
		form.setSenderContactInfo("");
		form.setPapers(Collections.emptyList());
		form.setProductRef(product.getSeq());
		
		groupOrderService.createGroupOrderForm(user.getSeq(), form, false);
		GroupOrderForms.unescape(form);
		return form;
	}
	/**
	 * 공동 구매 문의 수정(실제 주문이 아님)
	 * @param formSeq
	 * @param body
	 * @return
	 */
	public GroupOrderForm updateOrderForm(Integer formSeq, TypeMap body) {
		GroupOrderForm form = groupOrderService.findBy(GroupOrderForm.Column.seq, formSeq);
		GroupOrderForms.unescape(form);
		
		TypeMap content = Util.parseJson(om, form.getContent());
		String price = body.getStr("price");
		String qtt = body.getStr("qtt");
		
		content.update("price", price);
		content.update("qtt", qtt);
		
		form.setContent(Util.stringify(om, content));
		groupOrderService.updateContent(form);
		GroupOrderForms.unescape(form);
		
		return form;
	}
	/**
	 * 공동 구매 문의 삭제(실제 주문이 아님)
	 * @param formSeq
	 */
	public void deleteOrderForm(Integer formSeq) {
		GroupOrderForm form = groupOrderService.findBy(GroupOrderForm.Column.seq, formSeq);
		GroupOrderForms.unescape(form);
		
		GroupOrderDao dao = groupOrderService.getDao();
		dao.deleteOrderForm(form.getSeq());
	}
	/**
	 * 공동구매 참여자들마다 실제 주문을 생성함.
	 * 참여자들은 각자의 설정 화면에서 결제를 진행함
	 * @param admin
	 * @param productUuid
	 * @return
	 */
	public List<Order> createOrders(User admin, String productUuid) {
		Product product = productService.findBy(Product.Column.prod_code, productUuid);
		Products.assertGroupBuyingProduct(product);
		List<GroupOrderForm> forms = groupOrderService.findOrderForms(
				GroupOrderForm.Column.target_product_ref,
				product.getSeq());
		
		if (product.checkExpiration(Instant.now())) {
			throw new AppException(ErrorCode.GBUYING_ORDER_ERROR, 422, "PRODUCT_EXPIRED");
		}
		if (forms.isEmpty()) {
			// 문의 양식이 없다!
			throw new AppException(ErrorCode.GBUYING_ORDER_ERROR, 422, "NO_FORMS_REGSITERED");
		}
		
		OrderService orderService = groupOrderService.getOrderService();
		Map<GroupOrderForm, Order> orders = new HashMap<>(forms.size());
		for (GroupOrderForm form : forms) {
			GroupOrderForms.unescape(form);
			User user = form.getSender();
			TypeMap content = Util.parseJson(om, form.getContent());
			Integer price = content.asInt("price");
			Order data = orderService.createOrder(user.getSeq(), productUuid, 1, order -> {
				/* 
				 * createOrder는 소매 상품에 대한 주문을 생성함
				 * 제품가격 x 갯수로 최종 청구금액을 계산함
				 * 
				 * 공동구매 상품은 사용자별로 청구금액을 갖고 있으므로 여기서 반드시 수정해줘야함
				 */
				order.setTotalAmount(price);
			});
			orders.put(form, data);
		}
		for (GroupOrderForm form : orders.keySet()) {
			TypeMap content = Util.parseJson(om, form.getContent());
			TypeMap props = TypeMap.with(
					"form", form, "order", orders.get(form),
					"product", product,
					"content", content);
			MailDto mailtoClient = new MailDto(
					Util.join("[공동구매] ","[", product.getName(), "] 참여 안내"),
					"gbuying-order.client",
					props,
					adminEmail,
					form.getSenderEmail(),
					null, null);
			mailingService.sendMail(mailtoClient);
		}
		
		return new ArrayList<>(orders.values());
	}
}
