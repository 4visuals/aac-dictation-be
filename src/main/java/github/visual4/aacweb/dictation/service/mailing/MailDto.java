package github.visual4.aacweb.dictation.service.mailing;

import java.time.Instant;
import java.time.LocalDateTime;

import github.visual4.aacweb.dictation.TypeMap;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailDto {
    Integer seq;
    String title;
    String templateId;
    Object props;
    String sender;
    String receiver;
    Instant whenRegistered;
    Instant whenProcessed;
    
	public MailDto(String title, String templateId, Object props, String sender, String receiver,
			Instant whenRegistered, Instant whenProcessed) {
		super();
		this.title = title;
		this.templateId = templateId;
		this.props = props;
		this.sender = sender;
		this.receiver = receiver;
		this.whenRegistered = whenRegistered;
		this.whenProcessed = whenProcessed;
	}
    
    
    
    
}
