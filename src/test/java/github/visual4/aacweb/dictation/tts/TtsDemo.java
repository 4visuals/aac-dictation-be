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
import java.util.Arrays;
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
	@Rollback(false)
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
		
		Set<String> hashing = new HashSet<>(voiceDao.selectHashes());
		
//		String _text = "삼촌/물병/창문/낮/젖/보물찾기/낮잠/우리 모여서 윷놀이하자./윷놀이하자./옆집/쏟아지다/주의/계절/의지/예매/낚시/손바닥/학습장/맛살/물난리/본래/단련/신랑/난로/천천히/우연히/즐거이/높이";
		String _text = "점잖다/배앓이/꿇다/종이접기/달력/종이접기 달력";
		List<String> words = Arrays.asList(_text.split("/"));
		for (String word : words) {
			word  = word.trim();
			// System.out.println("[" + word + "]");
			String hash = Util.Hash.md5(word).toLowerCase();
			String url = "https://kr.object.ncloudstorage.com/aac-dict-bucket/voices2/@hash.mp3".replace("@hash", hash);
			if (hashing.contains(hash)) {
				System.out.printf("[>>>] skip: %s, %s\n", word, url);
			}
			else {
				voiceService.download(word);
				hashing.add(hash);
				System.out.printf("[%s] %s\n",word, url);		
				sleep(100);
			}
		}
		/*
		voiceService.download(text);
		String hash = Util.Hash.md5(text).toLowerCase();
		if (hashing.contains(hash)) {
			System.out.println("[existing] " + text);
		}
		else {
			voiceService.download(text);
			hashing.add(hash);
			
		}
		*/
		
	}
	
	@Test
	@Rollback(false)
	void pushSentence() {
		Set<String> hashing = new HashSet<>(voiceDao.selectHashes());
		List<String> text = sentenceDao.findAllText(1618);
	
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
