package github.visual4.aacweb.dictation.domain.order.aim_port;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.Util;
import github.visual4.aacweb.dictation.domain.order.Order;
import github.visual4.aacweb.dictation.domain.order.Order.OrderState;
import github.visual4.aacweb.dictation.domain.order.OrderService;
import github.visual4.aacweb.dictation.domain.order.PG;
import github.visual4.aacweb.dictation.domain.order.aim_port.AimportDriver.AimportResponse;
import github.visual4.aacweb.dictation.domain.order.aim_port.AimportHook.PayStatus;
import github.visual4.aacweb.dictation.domain.user.User;
import github.visual4.aacweb.dictation.domain.user.UserService;
import github.visual4.aacweb.dictation.service.mailing.MailDto;
import github.visual4.aacweb.dictation.service.mailing.MailingService;
import lombok.extern.slf4j.Slf4j;

/**
 * 아임포트에 대한 결제 처리 및 조회를 담당.
 * 
 * @author chminseo
 *
 */
@Service
@Transactional
@Slf4j
public class AimPortService {
	@Value("${aimport.api-key}")
	String importAPIKey;
	
	@Value("${aimport.secret-key}")
	String importAPISecret;
	
	@Value("${dictation.ncp.mailing.receiver}")
	String adminEmail;
	
	private final String HOST = "https://api.iamport.kr";
	
	final UserService userService;
	final OrderService orderService;
	final ObjectMapper om;
	final MailingService mailingService;

	private final AimportDriver portOneDriver;
	/**
	 * 결제를 승인한 관리자
	 */
	private final static Long CONFIRMER = 1L;
	
	
	public AimPortService(OrderService orderService,
			AimportDriver portOneDriver,
			ObjectMapper om,
			UserService userService,
			MailingService mailingService) {
		this.userService = userService;
		this.orderService = orderService;
		this.mailingService = mailingService;
		this.portOneDriver = portOneDriver;
		this.om = om;
	}
	/**
	 * 결제 시작 전 포트원에 금액 검증 정보를 전송함
	 * @see https://developers.portone.io/docs/ko/auth/guide/5/pre 
	 * @param order
	 */
	public void sendPaymentVerification(Order order) {
		String orderUuid = order.getOrderUuid();
		Integer amount = order.getTotalAmount();
		portOneDriver.preparePayment(orderUuid, amount);
		
	}
	/**
	 * 포트원에서 결제 결과 확인 - 결제승인, 결제취소(전체취소)
	 * 
	 * 
	 * @param hook
	 * @return 
	 */
	public Order confirmPayment(AimportHook hook) {
		log.info("[aimport uuid] {}", hook.aimportUuid);
		log.info("[order   uuid] {}", hook.orderUuid);
		
		AimportResponse remote = portOneDriver.queryPaymentDetail(hook.aimportUuid);
		
		String aimportUuid = remote.body.getStr("imp_uid");
		String orderUuid = remote.body.getStr("merchant_uid");
		String status = remote.body.getStr("status");
		PayStatus paymentStatus = PayStatus.valueOf(status);
		
		if (!hook.checkUuid(aimportUuid, orderUuid)) {
			log.error(String.format("[PAYMENT] uuid mismatch, hook(a:%s, o:%s) vs real(a:%s, o:%s)",
					hook.aimportUuid, hook.orderUuid,
					aimportUuid, orderUuid));
			throw new AppException(ErrorCode.PAYMENT_VALIDATION_ERROR, 409, "uuid mismatch");
		}
		if (!hook.checkStatus(paymentStatus)) {
			log.error(String.format("[PAYMENT] status mismatch hook(%s) vs real(%s)", hook.status, paymentStatus.name()));
			throw new AppException(ErrorCode.PAYMENT_VALIDATION_ERROR, 409, 
					String.format(
							"payment status mismatch. [%s] from requst, but [%s] from rest api",
							hook.getStatus(), paymentStatus.name()));
		}
		
		Order order = orderService.findOrder(orderUuid);
		log.info("[detail]", order.getTransactionDetail()); 
		if(paymentStatus == PayStatus.paid) {
			// PG에서 결제완료됨
			orderPaid(order, remote.body);
			return order;
			
		} else if(paymentStatus == PayStatus.cancelled) {
			// PG 관리자 화면에서 결제를 취소함
			orderCancelledByRemote(order, remote.body);
			return null;
		} else {
			return null;
		}
	}
	/**
	 * 결제 성공 후 관리자에게 메일 전송
	 * @param order 성공한 결제
	 */
	public void sendPaymentSuccessEmail(Order order) {
		User customer = order.getCustomer();
		if (customer == null) {
			customer = userService.findUser(order.getCustomerRef());
		}
		
		String sender = customer.getEmail();
		TypeMap props = TypeMap.with(
				"creationTimeKr", Util.toKoreanTime(order.getPaidTime()),
				"senderName", customer.getName(),
				"senderEmail", customer.getEmail(),
				"content", order.getTotalAmount());
		
		String receiver = adminEmail;
		MailDto dto = new MailDto(
				"[신규 결제 완료] 신규 결제", 
				"payment-success.admin", props, 
				sender, receiver, null, null );
		mailingService.sendMail(dto);
		
	}
	/**
	 * 결제 승인됨
	 * @param order - 대기 상태인 주문
	 * @param body
	 */
	private void orderPaid(Order order, TypeMap body) {
		Integer amount = body.asInt("amount"); //결제된 금액
		/*
		 * 아임포트의 uid
		 */
		String aimportUuid = body.getStr("imp_uid");
		/*
		 * 실제 PG사의 uid
		 */
		String endTxUid = body.getStr("pg_tid");
		/*R
		 * kdict 백엔드에서 생성한 orderUuid
		 */
		String orderUuid = body.getStr("merchant_uid");
		String pgVendor = body.getStr("pg_provider");
		
		PayStatus paymentStatus = PayStatus.valueOf(body.getStr("status"));
		if(paymentStatus != PayStatus.paid) {
			String msg = String.format("paid required but %s", paymentStatus.name());
			log.error(msg);
			throw new AppException(ErrorCode.PAYMENT_VALIDATION_ERROR, 409, msg);
		}
		if (!order.isPending()) {
			String msg = String.format("not a pending order, order state : %s", order.getOrderState());
			log.error(msg);
			throw new AppException(ErrorCode.PAYMENT_VALIDATION_ERROR, 409, msg);
		}
		
		Integer price = order.getTotalAmount();
		if (!price.equals(amount)) {
			/*
			 * 청구 금액과 실제 결제 금액이 다름
			 * 결제를 취소시킴
			 */
			String msg = String.format("paid amount mismatch: order: %s", orderUuid);
			log.error(msg);
			throw new AppException(ErrorCode.PAYMENT_VALIDATION_ERROR, 409, msg);
		}
		
		/*
		 * (x): 1주문당 1개의 수강증 발급으로 다시 변경(원래 2장이었음)
		 * (o): 주문마다 라이선스 갯수를 담도록 변경함(2024-01-24)
		 */
		final Integer numOfLicenses = order.getLicenseQtt();
		final String detail = Util.stringify(om, body);
		
		orderService.activateOrder(order.getOrderUuid(), CONFIRMER, numOfLicenses, odr -> {
			odr.setMidTransactionUid(aimportUuid);
			odr.setEndTransactionUid(endTxUid);
			odr.setOrderState(OrderState.ATV);
			odr.setPaygateVendor(PG.valueOf(pgVendor));
			odr.setTransactionDetail(detail);
		});
		
	}
	/**
	 * 포트원 관리자 화면에서 결제를 취소했음
	 * @param order
	 * @param body
	 */
	private void orderCancelledByRemote(Order order, TypeMap body) {
		
		Integer cancelledAmount = body.getInt("cancel_amount");
		/*
		 * kdict 백엔드에서 생성한 주문 식별 코드
		 */
		String orderUuid = body.getStr("merchant_uid");
		if (!order.isActive()) {
			String msg = String.format("cannot cancel order(expected active, but %s)", order.getOrderState());
			log.error(msg);
			throw new AppException(ErrorCode.PAYMENT_VALIDATION_ERROR, 409, msg);
		}
		// FIXME 주문 취소된거 확인함. 발급된 이용권 취소시켜야 함
		Integer price = order.getTotalAmount();
		if (!price.equals(cancelledAmount)) {
			/*
			 * 결제했던 금액과 취소 금액이 다름
			 * 결제를 취소시킴
			 */
			String msg = String.format("cancel amount mismatch. expected: %d, but %d", price, cancelledAmount);
			log.error(msg);
			throw new AppException(ErrorCode.PAYMENT_VALIDATION_ERROR, 409, msg);
		}
		
		orderService.cancelActiveOrder(orderUuid, OrderState.CNR, false);
	}
	
	
	
}
