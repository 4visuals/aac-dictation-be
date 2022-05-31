package github.visual4.aacweb.dictation.tts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.common.hash.Hashing;

import github.visual4.aacweb.dictation.Util;
import github.visual4.aacweb.dictation.config.AacDictationConfig;
import github.visual4.aacweb.dictation.domain.exam.EojeolAnswerDao;
import github.visual4.aacweb.dictation.domain.sentence.SentenceDao;
import github.visual4.aacweb.dictation.domain.voice.VoiceDao;
import github.visual4.aacweb.dictation.domain.voice.VoiceService;


@ActiveProfiles("dev")
@ExtendWith(SpringExtension.class)
@Import({AacDictationConfig.class, VoiceService.class})
@BootstrapWith(SpringBootTestContextBootstrapper.class)
class TtsDemo{

	@Value("${dictation.aws.access-key}") String accessKey;
	@Value("${dictation.aws.secret-key}") String secretKey;
	
	@Autowired VoiceService voiceService;
	@Autowired VoiceDao voiceDao;
	
	@Autowired
	SentenceDao sentenceDao;
	
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
		
		voiceService.download("나비야,");
		
	}
	
	@Test
	@Rollback(false)
	void pushSentence() {
		/*
		 * 탔어요 - L20 L22
		 * 휘파람 - L25 L25
		 * 다람쥐 - L25 L25
		 * 시계 - L27 L27
1884dc306af41e2f502d0259bc4457fd:서로 조금씩만 양보하렴.
c726b3ea61ca891ed8614e3ae993a3c9:내가 넘어가는 것 같다.
ff9a35162db97bac12caac5c1710b754:한숨을 푹 쉬며 말했어요.
838e415582587ae1f90875492b98a461:큰 소리로 책을 읽고 있었다.
5c5d846c0bdd901a91ac226457262ff4:서로 바라보며 웃기 시작했다.
4736a92e46fcfa0b485083c3140eac21:칭찬 딱지를 붙여 주셨다.
0902a50ed4227385ce937135c8821c11:학교 뒤뜰에 있는 텃밭
7f5d45af96b5d9fa25078e4d9113e8d9:현관에 세워 놓은 자전거
0319289724340802d6e282432e1f8fa7:창문을 쾅 닫았습니다.
		 *
		 */
		Set<String> hashing = new HashSet<>(voiceDao.selectHashes());
		List<String> text = sentenceDao.findAllText(1885);
	
		List<String> dup = new ArrayList<>();
		int seq = 1;
		for(String txt : text) {
			String hash = Util.Hash.md5(txt).toLowerCase();
			if (hashing.contains(hash)) {
				dup.add(hash + ":"+ txt );
			}
			else {
				System.out.printf("[%4d] %s\n", seq, txt);
				voiceService.download(txt);
				hashing.add(hash);
				sleep(100);
				
			}
		}
		System.out.println("[dup]");
		dup.forEach(System.out::println);
	}
	
	@Test
	@Rollback(false)
	void pushEojeols() throws SQLException {
		Set<String> hashing = new HashSet<>(voiceDao.selectHashes());
		List<String> text = new ArrayList<>();//sentenceDao.findAllText(1885);
		
		Connection con = DriverManager.getConnection("jdbc:mariadb://localhost:3306/aacdictdb", "root", "1111");
		PreparedStatement stmt = con.prepareStatement("select text from wr_eojeol");
		
		ResultSet rs = stmt.executeQuery();
		while(rs.next()) {
			text.add(rs.getString("text"));
		}
		con.close();
		List<String> dup = new ArrayList<>();
		int seq = 1;
		for(String txt : text) {
			String hash = Util.Hash.md5(txt).toLowerCase();
			if (hashing.contains(hash)) {
				dup.add(hash + ":"+ txt );
			}
			else {
				System.out.printf("[%4d] %s\n", seq, txt);
				voiceService.download(txt);
				hashing.add(hash);
				sleep(50);
				
			}
		}
		System.out.println("[dup]");
		dup.forEach(System.out::println);
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
