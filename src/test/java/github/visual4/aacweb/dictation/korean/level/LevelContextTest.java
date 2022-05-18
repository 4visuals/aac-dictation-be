package github.visual4.aacweb.dictation.korean.level;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import github.visual4.aacweb.dictation.Util;
import github.visual4.aacweb.dictation.korean.Mark;

class LevelContextTest {

	@Test
	void test() throws ClassNotFoundException {
		Class<Level34> cls = (Class<Level34>) Class.forName("github.visual4.aacweb.dictation.korean.level.Level32");
		LevelContext ctx =new LevelContext();
		Level34 lvl = Util.createInstance(cls, new Object[] {ctx}, LevelContext.class);
		assertNotNull(lvl);
		System.out.println(ctx);
	}
	
	@Test
	public void sen_331 () {
		LevelContext ctx = new LevelContext();
		Mark mark = ctx.parseDifficulties("살금살금 다가가 양말을 벗겨요. ");
		System.out.println(mark.toMap());
		
	}

}
