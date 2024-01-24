package github.visual4.aacweb.dictation.domain.order.aim_port;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.Util;

/**
 * 아임포트(포트원) rest api driver
 * 
 * @author chminseo
 *
 */
@Component
public class AimportDriver {
	@Value("${aimport.api-key}")
	String importAPIKey;
	
	@Value("${aimport.secret-key}")
	String importAPISecret;
	
	private final String HOST = "https://api.iamport.kr";

	private AccessToken token;
	
	@Autowired
	ObjectMapper om;
	
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
//		System.out.printf("LocalNow: %d, now: %d, exp: %d\n", localNow, nowMillis, expMillis);
		String accessToken = res.body.getStr("access_token");
		AccessToken token = new AccessToken(localNow, durationInMillis, accessToken);
//		System.out.println(token);
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
	public static class AimportResponse {
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
			Map<String, Object>response = (Map<String, Object>) res.get("response");
			if (response == null) {
				response = Collections.emptyMap();
			}
			TypeMap body = new TypeMap(response);
			return new AimportResponse(code, message, body);
		}
	}
	
	AimportResponse get(String endPoint) {
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
		return AimportResponse.pase(response);
	}
	
	AimportResponse post(String endPoint, TypeMap bodies) {
		String accessToken = getAccesstoken();
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", accessToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity request = new HttpEntity(Util.stringify(om, bodies), headers);
		
		RestTemplate rest = new RestTemplate();
		Map r = rest.postForObject(
				HOST + endPoint,
				request,
				Map.class);
		return AimportResponse.pase(r);
		
	}
	/**
	 * [결제 정보 사전 등록]
	 * 사용자가 상품을 선택해서 결제 팝업을 띄울때 백엔드에서 생성한 주문의 orderUuid(merchant_uid)와 가격을 아임포트로 전송함.
	 * 프론트엔드에서 가격을 조작할 경우 아임포트에서 부정 결제로 처리됨.
	 * 
	 * @param merchantUuid - 결제 프로세스를 시작할때 생성한 Order의 orderUUid값
	 * @param amountKR - 프론트엔드에서 사용자가 선택한 제품의 가격
	 * @see <a href="https://developers.portone.io/docs/ko/auth/guide/5/pre">https://developers.portone.io/docs/ko/auth/guide/5/pre</a>
	 * @return
	 */
	public AimportResponse preparePayment(String merchantUuid, Integer amountKR) {
		return this.post("/payments/prepare", TypeMap.with(
				"merchant_uid", merchantUuid,
				"amount", amountKR));
		
	}
	/**
	 * 결제 상세 정보 사후 검증
	 * @param aimportUuid - 포트원 거래번호. 결제/상세내역조회 메뉴에서 imp_xxxxxx 형태의 키값(UID)
	 * @see <a href="https://developers.portone.io/docs/ko/auth/guide/5/post">https://developers.portone.io/docs/ko/auth/guide/5/post</a>
	 * @return
	 */
	public AimportResponse queryPaymentDetail(String aimportUuid) {
		return this.get("/payments/" + aimportUuid);
	}
	/**
	 * 승인된 결제 취소
	 * @param aimportUuid - 포트원 거래번호. 결제/상세내역조회 메뉴에서 imp_xxxxxx 형태의 키값(UID)
	 * @apiNote - api 요청으로 승인된 결제를 취소하는 경우에는 처리 결과가 webhook으로 호출되지 않음
	 * @return
	 */
	public AimportResponse cancelPayment(String aimportUuid) {
		return null;
	}
	
}
