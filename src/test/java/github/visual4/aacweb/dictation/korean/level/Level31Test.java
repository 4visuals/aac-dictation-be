package github.visual4.aacweb.dictation.korean.level;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Mark;
import github.visual4.aacweb.dictation.korean.Mark.Pos;

class Level31Test {

	@Test
	void test() {
		LevelContext ctx = new LevelContext();
		Level31 lvl = new Level31(ctx);
		Mark mark = lvl.eval("단풍잎");
		mark.eachLevel((Difficulty df, Pos poses) -> {
			poses.each(pos -> {
				System.out.println(Arrays.toString(pos));
			});
		});
	}

}
