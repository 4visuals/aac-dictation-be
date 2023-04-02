package github.visual4.aacweb.dictation.service.sms;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.databind.ObjectMapper;

import github.visual4.aacweb.dictation.BaseDao;

@Import({NcpSmsService.class})
class NcpSmsServiceTest  extends BaseDao{

	@Autowired
	NcpSmsService smsService;
	@Test
	void test() {
		assertNotNull(smsService);
		
		smsService.sendSms("01012345678", "for api test");
	}

	@Configuration
	static class ObjectMapperConfig {
		@Bean
		public ObjectMapper om() {
			return new ObjectMapper();
		}
	}
}
