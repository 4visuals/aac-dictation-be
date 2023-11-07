package github.visual4.aacweb.dictation.domain.order.gbuying;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.Util;
import github.visual4.aacweb.dictation.domain.order.GroupOrderForms;
import github.visual4.aacweb.dictation.domain.order.group.GroupOrderDao;
import github.visual4.aacweb.dictation.domain.order.group.GroupOrderForm;
import github.visual4.aacweb.dictation.domain.order.group.GroupOrderService;
import github.visual4.aacweb.dictation.domain.product.Product;
import github.visual4.aacweb.dictation.domain.product.ProductService;
import github.visual4.aacweb.dictation.domain.user.User;
import github.visual4.aacweb.dictation.domain.user.UserService;

@Service
@Transactional
public class GroupBuyingOrderService {
	final UserService userService;
	final ProductService productService;
	final GroupOrderService groupOrderService;
	final ObjectMapper om;
	
	public GroupBuyingOrderService(
			UserService userService, 
			ProductService productService,
			GroupOrderService groupOrderService,
			ObjectMapper om) {
		this.userService = userService;
		this.productService = productService;
		this.groupOrderService = groupOrderService;
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
	 * 공동구매 주문 양식 등록(실제 주문이 아님)
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
		if(!product.checkIfGroupBuying()) {
			throw new AppException(ErrorCode.GROUP_ORDER_ERROR, 422, "not_a_groupbuying_product");
		}
		List<GroupOrderForm> forms = groupOrderService.findOrderForms(GroupOrderForm.Column.target_product_ref, product.getSeq());
		
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
	public void deleteOrderForm(Integer formSeq) {
		GroupOrderForm form = groupOrderService.findBy(GroupOrderForm.Column.seq, formSeq);
		GroupOrderForms.unescape(form);
		
		GroupOrderDao dao = groupOrderService.getDao();
		dao.deleteOrderForm(form.getSeq());
	}
}
