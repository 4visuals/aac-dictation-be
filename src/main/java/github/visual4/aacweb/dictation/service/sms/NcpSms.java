package github.visual4.aacweb.dictation.service.sms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NcpSms {

	public enum SmsType {
		SMS
	};
	
	SmsType type = SmsType.SMS;
	final String contentType = "COMM"; // 
	EndPoint sender;
	EndPoint receiver;
	String content;
	
	public static class EndPoint {
		
		String phoneNumber;
		String email;
		String name;
	}
}
