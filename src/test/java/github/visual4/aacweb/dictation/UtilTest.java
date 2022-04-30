package github.visual4.aacweb.dictation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import github.visual4.aacweb.dictation.korean.level.Level32;
import github.visual4.aacweb.dictation.korean.level.LevelContext;

class UtilTest {

	@Test
	void test() throws ClassNotFoundException {
		Class<Level32> cls = (Class<Level32>) Class.forName("github.visual4.aacweb.dictation.korean.level.Level32");
		LevelContext ctx =new LevelContext(null);
		Level32 lvl = Util.createInstance(cls, new Object[] {ctx}, LevelContext.class);
		assertNotNull(lvl);
	}

}
