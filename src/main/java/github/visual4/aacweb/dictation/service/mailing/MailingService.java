package github.visual4.aacweb.dictation.service.mailing;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.samskivert.mustache.Mustache;

import github.visual4.aacweb.dictation.service.template.TemplateService;

@Service
public class MailingService {

	final TemplateService templateService;
	final NcpMailingService mailSender;
	final com.samskivert.mustache.Mustache.Compiler c;
	public MailingService(
			NcpMailingService mailSender,
			TemplateService templateService) {
		this.templateService = templateService;
		this.mailSender = mailSender;
		c = Mustache.compiler();
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void sendMail(MailDto dto) {
		// String body = "<html><body><style>h3{color:red}</style><h3>단체구매 메일 전송 테스트2</h3></body></html>";
		String template = templateService.loadTemplate(dto.getTemplateId());
		String body = c.compile(template).execute(dto.getProps());
		Mail mail = new Mail(dto.getSender(), dto.getReceiver(), dto.getTitle(), body );
		mailSender.sendMail(mail);
	}
	
}
