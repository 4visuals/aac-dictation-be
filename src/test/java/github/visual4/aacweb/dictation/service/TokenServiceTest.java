package github.visual4.aacweb.dictation.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.BootstrapWith;

import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.config.AacDictationConfig;
import github.visual4.aacweb.dictation.service.codec.RsaCodec;



@ActiveProfiles("test")
@SpringBootTest
class TokenServiceTest {

	@Autowired
	AacDictationConfig config;
	
	@Autowired
	TokenService tokenService;
	
	@Test
	void test() {
		TypeMap props = TypeMap.with("email", "hello@gmail.com");
		String jwt = tokenService.generateJwt(props);
		System.out.println("[jwt]" + jwt);
		
		TypeMap data = tokenService.parseJwt(jwt);
		System.out.println(data);
		
	}

	@Test
	public void test_date() {
		Date d = new Date();
		String s = d.toString();
		System.out.println(s);
	}
}
