package github.visual4.aacweb.dictation.service.mailing;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Mustache.Compiler;
import com.samskivert.mustache.Template;

import github.visual4.aacweb.dictation.BaseDao;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.order.group.GroupOrderForm;
import github.visual4.aacweb.dictation.domain.order.group.OrderPaper;
import github.visual4.aacweb.dictation.domain.order.group.OrderPaper.PaperType;
import github.visual4.aacweb.dictation.service.template.TemplateService;

@Import(MailingServiceTest.Imports.class)
public class MailingServiceTest extends BaseDao{

	@Import({MailingService.class, NcpMailingService.class, TemplateService.class})
	public static class Imports {}
	
	@Autowired
	MailingService mailingService;
	
	@Autowired
	TemplateService templateService;
	
	
	private GroupOrderForm sampleOrder() {
		GroupOrderForm order = new GroupOrderForm();
		order.setSenderName("펭수");
		order.setSenderEmail("pengsu@ebs.co.kr");
		order.setOrgName("EBS");
		order.setOrgEmail("admin@ebs.co.kr");
		order.setContent("1년권 5장 신청합니다.");
//		order.setSenderContactInfo("010-3645-3883");
		order.setPapers(Arrays.asList(
				paperOf(PaperType.EST, "견적서"),
				paperOf(PaperType.ETC, "보증보험")));
		
		return order;
	}
	@Test
	void test_sendMail() {
		GroupOrderForm order = sampleOrder();
		MailDto dto =new MailDto("test", "order", order , order.getSenderEmail(), "yeori.seo@gmail.com", null, null);
		mailingService.sendMail(dto);
	}
	
	private OrderPaper paperOf(PaperType type, String desc) {
		OrderPaper p = new OrderPaper();
		p.setPaperType(type);
		p.setDesc(desc);
		return p;
	}

	@Test
	public void test_mustache() {
		Compiler c = Mustache.compiler();
		String template = templateService.loadTemplate("order");		
		Template p = c.compile(template);
		
		GroupOrderForm order = sampleOrder();
		order.setPapers(null);
		System.out.println(p.execute(order));
		
	}

}
