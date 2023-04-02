package github.visual4.aacweb.dictation.service.sms;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import github.visual4.aacweb.dictation.BaseDao;
import github.visual4.aacweb.dictation.domain.order.group.GroupOrderForm;
import github.visual4.aacweb.dictation.domain.order.group.GroupOrderService;
import github.visual4.aacweb.dictation.domain.order.group.OrderPaper;
import github.visual4.aacweb.dictation.domain.order.group.OrderPaper.PaperType;
import github.visual4.aacweb.dictation.service.template.TemplateService;

@Import({SmsService.class, NcpSmsService.class, TemplateService.class})
class SmsServiceTest extends BaseDao{

	@Autowired
	SmsService smsService;
	
	@MockBean
	GroupOrderService groupOrderService;
	
	@Test
	void test_sms_notification() {
		
		GroupOrderForm form = new GroupOrderForm();
		form.setSenderName("테스트");
		form.setSenderEmail("______@gmail.com");
		form.setSenderContactInfo("________");
		form.setState(GroupOrderForm.OrderFormState.PND);
		
		OrderPaper p0 = new OrderPaper();
		p0.setPaperType(PaperType.BNK);
		p0.setPaperType(PaperType.EST);
		p0.fillDesc();
		form.setPapers(Arrays.asList(p0, p0));
		
		smsService.notifyForOrderDocument(form, "paper-notif");
		
		
	}

}
