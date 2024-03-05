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
import java.util.Scanner;
import java.util.Set;

import org.junit.jupiter.api.Disabled;
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

import github.visual4.aacweb.dictation.BaseDao;
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
class TtsDemo {

	@Value("${dictation.aws.access-key}") String accessKey;
	@Value("${dictation.aws.secret-key}") String secretKey;
	
	@Autowired VoiceService voiceService;
	@Autowired VoiceDao voiceDao;
	
	@Autowired
	SentenceDao sentenceDao;
	
	@Test
	@Disabled
	@Rollback(false)
	void run() throws IOException {
//		Set<String> hashing = new HashSet<>(voiceDao.selectHashes());
		
//		String _text = "삼촌/물병/창문/낮/젖/보물찾기/낮잠/우리 모여서 윷놀이하자./윷놀이하자./옆집/쏟아지다/주의/계절/의지/예매/낚시/손바닥/학습장/맛살/물난리/본래/단련/신랑/난로/천천히/우연히/즐거이/높이";
//		String _text = "언니가 사진을 찍습니다.";
		List<String> words = new ArrayList<>();
		InputStream in = this.getClass().getResourceAsStream("word.txt");
		Scanner sc = new Scanner(in);
		while(sc.hasNextLine()) {
			words.add(sc.nextLine().trim());
		}
		sc.close();
		
		publish(words);
		
	}
	
	void publish(List<String> words) {
		Set<String> hashing = new HashSet<>(voiceDao.selectHashes());
		List<String> dup = new ArrayList<>();
		for (String word : words) {
			word  = word.trim();
			String hash = Util.Hash.md5(word).toLowerCase();
			String url = "https://kr.object.ncloudstorage.com/aac-dict-bucket/voices3/@hash.mp3".replace("@hash", hash);
			if (hashing.contains(hash)) {
				dup.add(hash + ":"+word);
			}
			else {
				voiceService.download(word);
				hashing.add(hash);
				System.out.printf("[%s] %s\n",word, url);		
				sleep(100);
			}
		}
		System.out.println("[dup]");
		System.out.println(dup.size());
		for (String hash : hashing) {
			String url = "https://kr.object.ncloudstorage.com/aac-dict-bucket/voices3/@hash.mp3".replace("@hash", hash);
			System.out.println(url);
		}
	}
	
	@Test
	@Disabled
	@Rollback(false)
	void pushSentence() {
		Set<String> hashing = new HashSet<>(voiceDao.selectHashes());
		List<String> text = sentenceDao.findAllText(0);
	
		List<String> dup = new ArrayList<>();
		int seq = 1;
		for(String txt : text) {
		    txt = txt.trim();
			String hash = Util.Hash.md5(txt).toLowerCase();
			if (hashing.contains(hash)) {
				dup.add(hash + ":"+ txt );
			}
			else {
				System.out.printf("[%4d] %s\n", seq++, txt);
				voiceService.download(txt);
				hashing.add(hash);
				sleep(50);
			}
		}
		System.out.println("[dup]");
		System.out.println(dup.size());
	}
	
	@Test
	void listSentences() {
	    List<String> text = sentenceDao.findAllText(1618);
	    System.out.println(text.size());
	}
	@Test
	@Disabled
	@Rollback(false)
	void pushEojeols() throws SQLException {
		Set<String> hashing = new HashSet<>(voiceDao.selectHashes());
		
		Connection con = DriverManager.getConnection("jdbc:mariadb://localhost:3306/aacdictdb", "root", "1111");
		PreparedStatement stmt = con.prepareStatement("select seq, text from wr_eojeol");
		
		List<Object[]> eojeols = new ArrayList<>();//sentenceDao.findAllText(1885);
		ResultSet rs = stmt.executeQuery();
		while(rs.next()) {
		    eojeols.add(new Object[] { rs.getInt("seq"), rs.getString("text").trim()});
			
		}
		con.close();
		List<String> dup = new ArrayList<>();
		int seq = 1;
		for(Object [] ej : eojeols) {
		    Integer ejSeq = (Integer) ej[0];
		    String text = (String) ej[1];
			String hash = Util.Hash.md5(text).toLowerCase();
			if (hashing.contains(hash)) {
				dup.add(hash + ":"+ text + ":" + ejSeq );
			}
			else {
				System.out.printf("[%4d] %s(%d)\n", seq++, text, ejSeq);
				voiceService.download(text);
				hashing.add(hash);
				sleep(50);
				
			}
		}
		System.out.println("[dup]");
		System.out.println(dup.size());
	}
	
	@Test
	@Rollback(false)
	void testAddTestSentence() {
		String text="테스트 하나 둘 셋 넷";
//		String hash = Util.Hash.md5(text).toLowerCase();
		voiceService.download(text);
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
