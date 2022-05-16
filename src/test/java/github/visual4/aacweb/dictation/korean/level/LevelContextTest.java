package github.visual4.aacweb.dictation.korean.level;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import github.visual4.aacweb.dictation.Util;

class LevelContextTest {

	@Test
	void test() throws ClassNotFoundException {
		Class<Level34> cls = (Class<Level34>) Class.forName("github.visual4.aacweb.dictation.korean.level.Level32");
		LevelContext ctx =new LevelContext();
		Level34 lvl = Util.createInstance(cls, new Object[] {ctx}, LevelContext.class);
		assertNotNull(lvl);
		System.out.println(ctx);
	}

}
