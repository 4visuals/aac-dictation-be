package github.visual4.aacweb.dictation.domain.order.group;

import java.time.Instant;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.Util;
import github.visual4.aacweb.dictation.domain.license.LicenseService;
import github.visual4.aacweb.dictation.domain.order.Order;
import github.visual4.aacweb.dictation.domain.order.OrderCommitDto;
import github.visual4.aacweb.dictation.domain.order.OrderService;
import github.visual4.aacweb.dictation.domain.order.PG;
import github.visual4.aacweb.dictation.domain.order.Order.OrderState;
import github.visual4.aacweb.dictation.domain.order.group.GroupOrderForm.OrderFormState;
import github.visual4.aacweb.dictation.domain.product.Product;
import github.visual4.aacweb.dictation.domain.product.ProductService;
import github.visual4.aacweb.dictation.domain.user.User;
import github.visual4.aacweb.dictation.domain.user.UserService;
import github.visual4.aacweb.dictation.service.UuidService;
import github.visual4.aacweb.dictation.service.mailing.MailDto;
import github.visual4.aacweb.dictation.service.mailing.MailingService;

@Service
@Transactional
public class GroupOrderService {
	final String host;
	final String receiverMail;
	
	final OrderService orderService;
	final GroupOrderDao groupOrderDao;
	final GroupPaperDao paperDao;
	final MailingService mailingService;
	final UuidService uuidService;
	final UserService userService;
	final LicenseService licenseService;
	final ProductService productService;
	final ObjectMapper om;
	
	private final static String ORDER_URL = "@host/console";
	public GroupOrderService(
			@Value("${dictation.host}") String host,
			@Value("${dictation.ncp.mailing.receiver}") String receiverMail,
			OrderService orderService,
			GroupPaperDao paperDao,
			GroupOrderDao groupOrderDao,
			MailingService mailingService,
			UuidService uuidService, 
			UserService userService,
			LicenseService licenseService,
			ProductService productService, 
			ObjectMapper om) {
		this.host = host;
		this.receiverMail = receiverMail;
		this.orderService = orderService;
		this.groupOrderDao = groupOrderDao;
		this.paperDao = paperDao;
		this.mailingService = mailingService;
		this.uuidService = uuidService;
		this.userService = userService;
		this.licenseService = licenseService;
		this.productService = productService;
		this.om = om;
	}
	/**
	 * 단체 구매 문의 등록
	 * @param teacherSeq 
	 * @param orderForm
	 * @return
	 */
	public GroupOrderForm createGroupOrderForm(Long teacherSeq, GroupOrderForm orderForm) {
		
		User teacher = userService.findTeacher(teacherSeq.longValue());
		if (teacher == null) {
			throw new AppException(ErrorCode.NOT_A_MEMBER, 400);
		}
		/* 1, 2, 3 순서 바꾸면 안됨*/
		
		/* 1 */
		orderForm.setCreationTime(Instant.now());
		orderForm.setState(OrderFormState.PND);
		orderForm.setSender(teacher);
		
		/* 2 */
		String uuid = uuidService.createUuid(orderForm);
		orderForm.setOrderUuid(uuid);
		
		/* 3 */
		String fullUrl = ORDER_URL.replace("@host", host);
		orderForm.setOrderFullUrl(fullUrl);
		
		escapeForm(orderForm);
		
		groupOrderDao.createGroupOrderForm(orderForm);
		for (OrderPaper paper : orderForm.getPapers()) {
			paper.fillDesc();
			paperDao.insertOrderPaper(orderForm.getSeq(), paper);
		}
		orderForm.setSender(teacher);
		/*
		 * 4. 관리자에게 메일 통보
		 */
		MailDto mailToAdmin = new MailDto(
				"[신규 문의] 단체 구매 " + orderForm.getSenderEmail(), 
				"group-order-admin", 
				orderForm, 
				orderForm.getSenderEmail(), 
				receiverMail, null, null);
		mailingService.sendMail(mailToAdmin);
		/*
		 * 5. 신청자에게도 메일 통보
		 */
		MailDto mailtoClient = new MailDto(
				"[받아쓰기] 단체 구매 접수 완료",
				"group-order-client",
				orderForm,
				receiverMail,
				orderForm.getSenderEmail(),
				null, null);
		mailingService.sendMail(mailtoClient);
		return orderForm;
	}
	private void escapeForm(GroupOrderForm form) {
		form.setContent(StringEscapeUtils.escapeHtml4(form.getContent()));
		form.setOrgEmail(StringEscapeUtils.escapeHtml4(form.getOrgEmail()));
		form.setOrgName(StringEscapeUtils.escapeHtml4(form.getOrgName()));
		form.setSenderContactInfo(StringEscapeUtils.escapeHtml4(form.getSenderContactInfo()));
		form.setSenderEmail(StringEscapeUtils.escapeHtml4(form.getSenderEmail()));
		form.setSenderName(StringEscapeUtils.escapeHtml4(form.getSenderName()));
		
		for (OrderPaper paper : form.getPapers()) {
			paper.setDesc(StringEscapeUtils.escapeHtml4(paper.getDesc()));
		}
		
	}
	public List<GroupOrderForm> findOrdersByState(OrderFormState state) {
		List<GroupOrderForm> orders = groupOrderDao.findOrders(GroupOrderForm.Column.group_order_state, state);
		for (GroupOrderForm order : orders) {
			escapeForm(order);
		}
		return orders;
	}
	/**
	 * 단체 구매 문의 취소
	 * @param orderSeq
	 * @param formState - 취소 사유
	 * @return
	 */
	public GroupOrderForm cancelGroupOrder(Integer orderSeq, OrderFormState formState) {
		GroupOrderForm form = groupOrderDao.findBy(GroupOrderForm.Column.seq, orderSeq);
		form.cancel(formState);
		groupOrderDao.cancelOrder(form);
		return form;
	}
	/**
	 * 단체 구매 처리
	 * - 주문 생성
	 * - 결제 처리
	 * - 수강증 발급
	 * - 안내 메일
	 * - 문자 등
	 * @param dto
	 * @return
	 */
	public Order commitOrder(Long adminSeq, OrderCommitDto dto) {
		GroupOrderForm form = groupOrderDao.findBy(GroupOrderForm.Column.seq, dto.getGroupOrderSeq());
		Product product = productService.findBy(Product.Column.prod_code, dto.getProductCode());
		Integer qtt = dto.getQtt();
		
		if (form.getState() != GroupOrderForm.OrderFormState.PND) {
			throw new AppException(ErrorCode.ORDER_ALREADY_ACTIVATED, 422);
		}
		if (qtt == null || qtt <= 0) {
			throw new AppException(ErrorCode.ORDER_INVALID_QTT, 422, "qtt: " + qtt);
		}
		if (product == null) {
			throw new AppException(ErrorCode.ORDER_INVALID_PROD, 404, dto.getProductCode());
		}
		if (product.isTrialProduct()) {
			throw new AppException(ErrorCode.ORDER_TRIAL_PROD, 422, dto.getProductCode());
		}
		if (product.checkExpiration(Instant.now())) {
			throw new AppException(ErrorCode.ORDER_EXPIRED_PROD, 422, dto.getProductCode());
		}
		if (dto.isInvalidPrice()) {
			throw new AppException(ErrorCode.ORDER_INVALID_PRICE, 422, dto.getProductCode());
		}
		
		Order order = orderService.createOrder(
				form.getSenderRef(),
				dto.getProductCode(),
				qtt, (odr) -> {
					odr.setTotalAmount(dto.getContractPrice());
					/*
					 * pg 벤더를 단체구매로 변경
					 */
					odr.setPaygateVendor(PG.group_order);
				});
		order = orderService.activateOrder(order.getOrderUuid(), adminSeq, qtt, (odr) -> {
			odr.setMidTransactionUid(PG.group_order.name());
			odr.setEndTransactionUid(PG.group_order.name());
			odr.setOrderState(OrderState.ATV);
			odr.setPaygateVendor(PG.group_order);
			odr.setTransactionDetail(createGroupOrderDetail(adminSeq, form, dto));
		});
		
		groupOrderDao.commitOrder(form);
		
		return order;
	}
	private String createGroupOrderDetail(Long adminSeq, GroupOrderForm form, OrderCommitDto commitDto) {
		TypeMap detail = TypeMap.with("admin", adminSeq, "form", form, "tx", commitDto);
		return Util.stringify(this.om, detail);
	}
}
