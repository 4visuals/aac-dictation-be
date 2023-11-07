package github.visual4.aacweb.dictation;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import github.visual4.aacweb.dictation.korean.level.Level34;
import github.visual4.aacweb.dictation.korean.level.LevelContext;

class UtilTest {

	@Test
	void test() throws ClassNotFoundException {
		Class<Level34> cls = (Class<Level34>) Class.forName("github.visual4.aacweb.dictation.korean.level.Level32");
		LevelContext ctx =new LevelContext();
		Level34 lvl = Util.createInstance(cls, new Object[] {ctx}, LevelContext.class);
		assertNotNull(lvl);
	}
	@Test
	void md5() {
		String hash = Util.Hash.md5("아침에 일어나").toLowerCase();
		System.out.println(hash);
		hash = Util.Hash.md5("공부를 했다.").toLowerCase();
		System.out.println(hash);
	}
	
	@Test
	void regexTest() {
		boolean matched = "student0993".matches(".*[a-zA-Z].*");
		assertTrue(matched);
		
		System.out.println(("odr-" + UUID.randomUUID().toString()));
	}
	
	@Test
	public void test_instant() {
		Instant t = Instant.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
		String time = formatter.format(t);
		System.out.println(time);
	}
	@Test
	public void test_exp2023() {
		Instant exp2023Kr = Instant.parse("2024-01-01T00:00:00Z").minus(9, ChronoUnit.HOURS);
		System.out.println(exp2023Kr.getEpochSecond());
		
	}
}
