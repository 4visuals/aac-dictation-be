package github.visual4.aacweb.dictation.tts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import github.visual4.aacweb.dictation.config.AacDictationConfig;
import github.visual4.aacweb.dictation.domain.voice.VoiceService;


@ActiveProfiles("dev")
@ExtendWith(SpringExtension.class)
@Import({AacDictationConfig.class, VoiceService.class})
@BootstrapWith(SpringBootTestContextBootstrapper.class)
class TtsDemo{

	@Value("${dictation.aws.access-key}") String accessKey;
	@Value("${dictation.aws.secret-key}") String secretKey;
	
	@Autowired VoiceService voiceService;
	
	@Test
	void run() throws IOException {
//		voiceService.download("우유 마셔요.");
//		voiceService.download("모두 여기로 모여.");
//		sleep(500);
//		voiceService.download("벼가 어서 자라서");
//		sleep(500);
//		voiceService.download("나비야, 이리 오너라.");
//		sleep(500);
//		voiceService.download("우리 야구하러 가요.");
//		sleep(500);
//		voiceService.download("이야기 나누자.");
//		sleep(500);
//		voiceService.download("테스트 문장입니다.");
//		sleep(500);
//		voiceService.download("비가 오려나?");
//		sleep(500);
//		voiceService.download("요요로 묘기 부리기");
//		sleep(500);
//		voiceService.download("여기서 기다려요.");
		
		
	}

	private void sleep(long t) {
		try {
			Thread.sleep(t);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
