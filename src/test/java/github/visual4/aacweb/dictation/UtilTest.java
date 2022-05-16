package github.visual4.aacweb.dictation;

import static org.junit.jupiter.api.Assertions.*;

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
		String hash = Util.Hash.md5("모두 여기로 모여. ");
		System.out.println(hash);
	}

}
