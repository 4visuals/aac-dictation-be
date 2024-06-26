package github.visual4.aacweb.dictation.domain.order;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.order.aim_port.AimPortService;
import github.visual4.aacweb.dictation.domain.order.aim_port.AimportHook;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class OrderHookController {
	
	@Autowired AimPortService aimportService;
	
	/**
	 * 아임포트 웹훅 처리
	 * 
	 * 유효한 ip는 다음과 같다.
	 * 
	 * 1) 52.78.100.19
	 * 2) 52.78.48.223
	 * 3) 52.78.5.241 (웹훅 테스트 발송 버튼으로  전송되는 경우)
	 * 
	 * @see https://chai-iamport.gitbook.io/iamport/result/webhook
	 * @param req
	 * @param info
	 * @return
	 */
	@PostMapping("/hooks/import")
	public Object hookFromIm_port(HttpServletRequest req, @RequestBody TypeMap info) {
		log.info("[PaymentHook] I'm port: " + info);
		String clientIp = req.getRemoteAddr();
		String xff = req.getHeader("X-Forwarded-For");
		log.info("[PaymentHook] client ip" + xff);
		log.info("[XFF      ]" + xff);
		
		if ("52.78.5.241".equals(xff)) {
			// Test 호출(관리자 화면에서 웹훅 설정을 테스트한 경우)
			log.warn("[PORTONE][WEBHOOK] Test Request! Did you test it?");
			return new ResponseEntity<>(HttpStatus.OK);
		}
		AimportHook hook = AimportHook
				.newBuilder()
				.clientIP(clientIp, xff)
				.uuid(info.getStr("imp_uid"), info.getStr("merchant_uid"))
				.status(info.getStr("status"))
				.build();
		
		Order order = aimportService.confirmPayment(hook);
		if(order != null) {
			aimportService.sendPaymentSuccessEmail(order);	
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
