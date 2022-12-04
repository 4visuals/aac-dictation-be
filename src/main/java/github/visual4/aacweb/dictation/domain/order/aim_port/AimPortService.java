package github.visual4.aacweb.dictation.domain.order.aim_port;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

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
import github.visual4.aacweb.dictation.domain.order.aim_port.AimportHook.PayStatus;
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
	String importAPIKey = "2544322837434222";
	String immportAPISecret = "zIQg9U2Pt0Mm3zBRTEPL4PNsajz3EhI0xrLfOV7lQH867fjynyyZCLB34EJdTYXFBRk3OUy36HQhpgHw";
	final private String HOST = "https://api.iamport.kr";
	private AccessToken token;
	
	OrderService orderService;
	final ObjectMapper om;
	
	public AimPortService(OrderService orderService, ObjectMapper om) {
		this.orderService = orderService;
		this.om = om;
	}
	/**
	 * 결제 확인
	 * 
	 * 
	 * @param hook
	 */
	public void confirmPayment(AimportHook hook) {
		log.info("[aimport uuid] {}", hook.aimportUuid);
		log.info("[order   uuid] {}", hook.orderUuid);
		
		AimportResponse res = get("/payments/" + hook.aimportUuid);
		System.out.println(res.body);
		/*
		 * {
		 * 	imp_uid=imp_249885652156,
		 *  merchant_uid=y5kfd34zbkg4tt292glx6,
		 *  status=paid,
		 *  amount=6900,
		 *  paid_at=1668495090,
		 *  pg_provider=danal_tpay,
		 *  pg_tid=202211151548057524183400,
		 *  
		 *		cancelled_at=0,
		 *		customer_uid=null,
		 *		channel=pc,
		 *		buyer_name=YN Seo, emb_pg_provider=null,
		 *		receipt_url=...,
		 *		cash_receipt_issued=false,
		 *		vbank_code=null, cancel_history=[], vbank_date=0, bank_name=null,
		 *		
		 *		escrow=false, currency=KRW, 
		 *		
		 *		card_quota=0, vbank_issued_at=0,
		 *		
		 *		user_agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36, bank_code=null,
		 *		buyer_email=yeori.seo@gmail.com, card_code=371,
		 *		amount=6900,
		 *		buyer_postcode=null, card_number=485480******5777, buyer_addr=null, customer_uid_usage=null, vbank_holder=null, vbank_name=null, pay_method=card, card_type=1, ,
		 *		
		 *		cancel_amount=0, cancel_reason=null, buyer_tel=null, cancel_receipt_urls=[], card_name=NH채움, name=1개월 이용권, apply_num=70929184,
		 *		pg_id=9810030929,
		 *		started_at=1668494885, vbank_num=null, failed_at=0, fail_reason=null, custom_data=null}
		 */
		String aimportUuid = res.body.getStr("imp_uid");
		String orderUuid = res.body.getStr("merchant_uid");
		String status = res.body.getStr("status");
		Integer amount = res.body.asInt("amount"); //결제된 금액
		Integer paidTime = res.body.asInt("paid_at"); // 결제 시간(초단위)
		String pgVendor = res.body.getStr("pg_provider");
		String endTxUid = res.body.getStr("pg_tid");
		
		if (!hook.checkUuid(aimportUuid, orderUuid)) {
			log.warn(String.format("[PAYMENT] uuid mismatch, hook(a:%s, o:%s) vs real(a:%s, o:%s)",
					hook.aimportUuid, hook.orderUuid,
					aimportUuid, orderUuid));
			return;
		}
//		if (status != "paid") {
//			log.warn(String.format("[[PAYMENT] status is %s (order: %s)", status, orderUuid));
//			return;
//		}
		if (!hook.checkStatus(PayStatus.valueOf(status))) {
			log.warn(String.format("[PAYMENT] status mismatch hook(%s) vs real(%s)", hook.status, status));
			return;
		}
		
		Order order = orderService.findOrder(orderUuid);
		if (!order.isPending()) {
			log.warn(String.format("[PAYMENT] not a RDY order, order state : %s", order.getOrderState()));
			return;
		}
		Integer price = order.getTotalAmount();
		if (!price.equals(amount)) {
			/*
			 * 청구 금액과 실제 결제 금액이 다름
			 * 결제를 취소시킴
			 */
			return;
		}
		order.setMidTransactionUid(aimportUuid);
		order.setEndTransactionUid(endTxUid);
		order.setOrderState(OrderState.ATV);
//		order.setConfirmerRef(1L);
//		order.setPaidTime(Instant.ofEpochSecond(paidTime));
		order.setPaygateVendor(pgVendor);
		
		String detail = Util.stringify(om, res.body);
		order.setTransactionDetail(detail);
		order = orderService.activateOrder(order.getOrderUuid(), 1L, null);
	}
	
	private AimportResponse get(String endPoint) {
		String accessToken = getAccesstoken();
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", accessToken);
		HttpEntity request = new HttpEntity(headers);
		
		RestTemplate rest = new RestTemplate();
		ResponseEntity<Map> r = rest.exchange(
				HOST + endPoint,
				HttpMethod.GET,
				request,
				Map.class);
		
		Map<String, Object> response = r.getBody();
		AimportResponse res = AimportResponse.pase(response);
		return res;
	}
	
	private AimportResponse connect(String method, String endPoint, TypeMap req) {
		boolean isGet = "get".equals(method);
		boolean isPost = "post".equals(method);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(HOST + endPoint);
		if (isGet && req != null) {
			req.forEach((key, value) -> {
				builder.queryParam(key, value.toString());
			});
		}
		URI uri = builder.encode().build().toUri();
		RestTemplate rest = new RestTemplate();
		
		Map<String, Object> response = null;
		
		if (isPost) {
			response = rest.postForObject(uri, req, HashMap.class);
		} else if (isGet) {
			response= rest.getForObject(uri, HashMap.class);
		} else {
			throw new AppException(ErrorCode.SERVER_ERROR, 500, "not supported http method(Aimport) :" + method );
		}
		
		AimportResponse res = AimportResponse.pase(response);
		return res;
	}
	private AccessToken issueAcessToken() {
		String endPoint = "/users/getToken";
		AimportResponse res = connect(
			"post",
			endPoint,
			TypeMap.with("imp_key", importAPIKey, "imp_secret", immportAPISecret));
		
		/* 
		 * {
		 *   code=0, 
		 *   response={
		 *     access_token=b08fee26c661d468f33d85d358c98cd10a41e791,
		 *     now=1670149240,
		 *     expired_at=1670149308
		 *   },
		 *   message=null}
		 */
		Long nowMillis = res.body.asLong("now") * 1000;
		Long expMillis = res.body.asLong("expired_at")* 1000;
		Long durationInMillis = expMillis - nowMillis; //
		Long localNow = System.currentTimeMillis();
		System.out.printf("LocalNow: %d, now: %d, exp: %d\n", localNow, nowMillis, expMillis);
		String accessToken = res.body.getStr("access_token");
		AccessToken token = new AccessToken(localNow, durationInMillis, accessToken);
		System.out.println(token);
		return token;
	}
	public String getAccesstoken() {
		
		if (token == null) {
			token = issueAcessToken();
		}
		if (!token.isValid()) {
			token = issueAcessToken();
		}
		return token.value;
	}
	
	private static class AccessToken {
		long issuedAt;
		long expiredAt;
		String value;
		
		AccessToken(Long now, Long duration, String accessToken) {
			this.issuedAt = now;
			this.expiredAt = now + duration;
			this.value = accessToken;
		}
		
		boolean isValid() {
			long remaining = expiredAt - System.currentTimeMillis();
			return remaining > 30 * 1000; // 30초
		}

		@Override
		public String toString() {
			return "AccessToken [now=" + issuedAt + ", expiredAt=" + expiredAt + ", token=" + value + "]";
		}
	}
	private static class AimportResponse {
		int code;
		String message;
		TypeMap body;
		
		public AimportResponse(int code, String message, TypeMap body) {
			super();
			this.code = code;
			this.message = message;
			this.body = body;
		}
		static AimportResponse pase(Map<String, Object> res) {
			Integer code = (Integer)res.get("code");
			String message = (String) res.get("message");
			TypeMap body = new TypeMap((Map<String, Object>)res.get("response"));
			return new AimportResponse(code, message, body);
		}
	}
}