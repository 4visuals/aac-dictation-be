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
	@Value("${aimport.api-key}")
	String importAPIKey;
	
	@Value("${aimport.secret-key}")
	String importAPISecret;
	
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
		 *  pg_tid=202....400,
		 */
		String aimportUuid = res.body.getStr("imp_uid");
		String orderUuid = res.body.getStr("merchant_uid");
		String status = res.body.getStr("status");
		Integer amount = res.body.asInt("amount"); //결제된 금액
		Integer paidTime = res.body.asInt("paid_at"); // 결제 시간(초단위)
		String pgVendor = res.body.getStr("pg_provider");
		String endTxUid = res.body.getStr("pg_tid");
		
		log.info("[paygate id] {}", pgVendor);
		
		if (!hook.checkUuid(aimportUuid, orderUuid)) {
			log.error(String.format("[PAYMENT] uuid mismatch, hook(a:%s, o:%s) vs real(a:%s, o:%s)",
					hook.aimportUuid, hook.orderUuid,
					aimportUuid, orderUuid));
			throw new AppException(ErrorCode.PAYMENT_VALIDATION_ERROR, 409, "uuid mismatch");
		}
		if (!hook.checkStatus(PayStatus.valueOf(status))) {
			log.error(String.format("[PAYMENT] status mismatch hook(%s) vs real(%s)", hook.status, status));
			throw new AppException(ErrorCode.PAYMENT_VALIDATION_ERROR, 409, 
					String.format(
							"payment status mismatch. [%s] from requst, but [%s] from rest api",
							hook.getStatus(), status));
		}
		
		Order order = orderService.findOrder(orderUuid);
		log.info("[detail]", order.getTransactionDetail());
		if (!order.isPending()) {
			log.error(String.format("[PAYMENT] not a RDY order, order state : %s", order.getOrderState()));
			throw new AppException(ErrorCode.PAYMENT_VALIDATION_ERROR, 409, "not a pending payment");
		}
		Integer price = order.getTotalAmount();
		if (!price.equals(amount)) {
			/*
			 * 청구 금액과 실제 결제 금액이 다름
			 * 결제를 취소시킴
			 */
			log.error(String.format("[PAYMENT] amount mismatch: order: " + orderUuid));
			throw new AppException(ErrorCode.PAYMENT_VALIDATION_ERROR, 409,
					String.format("amount mismatch. expected: %d, but %d", price, amount));
		}
		
		
		final String detail = Util.stringify(om, res.body);
		
		/*
		 * 1주문당 1개의 수강증 발급으로 다시 변경(원래 2장이었음)
		 */
		final Integer numOfLicenses = 1;
		
		orderService.activateOrder(order.getOrderUuid(), 1L, numOfLicenses, (odr) -> {
			odr.setMidTransactionUid(aimportUuid);
			odr.setEndTransactionUid(endTxUid);
			odr.setOrderState(OrderState.ATV);
			odr.setPaygateVendor(PG.valueOf(pgVendor));
			odr.setTransactionDetail(detail);
		});
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
			TypeMap.with("imp_key", importAPIKey, "imp_secret", importAPISecret));
		
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
