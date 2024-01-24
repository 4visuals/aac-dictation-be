package github.visual4.aacweb.dictation.service.sms;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.samskivert.mustache.Mustache;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.order.group.GroupOrderForm;
import github.visual4.aacweb.dictation.domain.order.group.GroupOrderService;
import github.visual4.aacweb.dictation.service.template.TemplateService;

@Service
public class SmsService {

	private static Set<String> TEMPALTES = Set.of("paper","license");
	final TemplateService templateService;
	final GroupOrderService groupOrderService;
	final NcpSmsService smsSender;
	final com.samskivert.mustache.Mustache.Compiler c;
	public SmsService(
			GroupOrderService groupOrderService,
			NcpSmsService smsSender,
			TemplateService templateService) {
		this.templateService = templateService;
		this.groupOrderService = groupOrderService;
		this.smsSender = smsSender;
		c = Mustache.compiler();
	}
	/**
	 * 단체 주문에 대해 문자 발송
	 * @param orderUuid - 단체주문 uuid
	 * @param body - 텍스트
	 */
	public void sendForGroupOrder(String orderUuid, String body) {
		GroupOrderForm form = groupOrderService.findBy(GroupOrderForm.Column.group_order_state, orderUuid);
		if (!form.isPendingState()) {
			throw new AppException(ErrorCode.GROUP_ORDER_ERROR, 422, "NOT_PENDING_STATE");
		}
		String phoneNumber = form.getSenderContactInfo();
		smsSender.sendSms(phoneNumber, body);
	}
	/**
	 * 단체 구매 문의 시 요청한 서류 발송을 알리는 문자 메세지.
	 * @param orderUuid
	 * @param body
	 * @return sms response 
	 */
	public TypeMap notifyForOrderDocument(String orderUuid, String templateName) {
		GroupOrderForm form = groupOrderService.findBy(
				GroupOrderForm.Column.group_order_uuid,
				orderUuid);
		if (!form.isPendingState()) {
			throw new AppException(ErrorCode.GROUP_ORDER_ERROR, 422, "NOT_PENDING_STATE");
		}
		String phoneNumber = form.getSenderContactInfo();
		String body = getSmsPreview(form.getOrderUuid(), templateName);
		return smsSender.sendSms(phoneNumber, body);
	}
	/**
	 * 단체구매자에게 문자 발송
	 * @param orderUuid
	 * @param body
	 * @return
	 */
	public Object notifyWithManualText(String orderUuid, String body) {
		GroupOrderForm form = groupOrderService.findBy(
				GroupOrderForm.Column.group_order_uuid,
				orderUuid);
		String phoneNumber = form.getSenderContactInfo();
		return smsSender.sendSms(phoneNumber, body);
	}
	
	public TypeMap notifyForOrderDocument(GroupOrderForm form, String templateName) {
//		GroupOrderForm form = groupOrderService.findBy(GroupOrderForm.Column.group_order_uuid, orderUuid);
		if (!form.isPendingState()) {
			throw new AppException(ErrorCode.GROUP_ORDER_ERROR, 422, "NOT_PENDING_STATE");
		}
		String phoneNumber = form.getSenderContactInfo();
		if(phoneNumber == null ) {
			throw new AppException(ErrorCode.SMS_PHONENUMBER_ERROR, 422, phoneNumber);
		}
		String body = getSmsPreview(form.getOrderUuid(), templateName);
		return smsSender.sendSms(phoneNumber, body);	
	}

	/**
	 * 문자 내용 미리 보기
	 * @param orderUuid 단체구매 uuid
	 * @return 
	 */
	public String getSmsPreview(String orderUuid, String templateName) {
		if (!TEMPALTES.contains(templateName)) {
			throw new AppException(ErrorCode.SMS_INVALID_TEMPLATE, 404, templateName);
		}
		GroupOrderForm form = groupOrderService.findBy(
				GroupOrderForm.Column.group_order_uuid,
				orderUuid);
		String template = templateService.loadTemplate("sms/" + templateName + "-notif");
		String body = c.compile(template).execute(form);
		return body;
	}
	

	
}
