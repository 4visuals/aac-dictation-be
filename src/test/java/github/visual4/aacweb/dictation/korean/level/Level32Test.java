package github.visual4.aacweb.dictation.korean.level;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import github.visual4.aacweb.dictation.korean.Mark;

class Level32Test {

	@Test
	void test() {
		LevelContext ctx = new LevelContext();
		Level32 lvl = new Level32(ctx);
		Mark mk = lvl.eval("것 같다");
		System.out.println(mk.toMap());
		
	}

}
