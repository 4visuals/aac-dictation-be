package github.visual4.aacweb.dictation.korean.level;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import github.visual4.aacweb.dictation.korean.Mark;

class Level30Test {

	@Test
	void test() {
		LevelContext ctx = new LevelContext();
		Level30 lvl = new Level30(ctx);
		Mark mk = lvl.eval("직업");
		System.out.println(mk.toMap());
	}

}
