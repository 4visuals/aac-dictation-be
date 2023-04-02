package github.visual4.aacweb.dictation.domain.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.visual4.aacweb.dictation.Res;
import github.visual4.aacweb.dictation.service.sms.SmsService;

/**
 * 
 * 메일, SMS등 관리자용 기능
 * @author chminseo
 *
 */
@RestController
@RequestMapping("/api/admin")
public class AdminMessagingController {

	@Autowired
	SmsService smsService;
	/**
	 * 단체 주문에서 요구한 서류 발송을 알리는 sms 전송
	 * @param uuid
	 * @return
	 */
	@PostMapping("/sms/group-order/{uuid}/{templateName}")
	public Object sendSmsPapper(@PathVariable String uuid, @PathVariable String templateName) {
		Object smsResponse = smsService.notifyForOrderDocument(uuid, templateName);
		return Res.success("sms", smsResponse);
	}
	
	@GetMapping("/sms/group-order/{uuid}/{templateName}/preview")
	public Object previewSms(@PathVariable String uuid, @PathVariable String templateName) {
		String smsText = smsService.getSmsPreview(uuid, templateName);
		return Res.success("sms", smsText);
	}
	
}
