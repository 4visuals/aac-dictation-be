package github.visual4.aacweb.dictation.korean;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import github.visual4.aacweb.dictation.korean.level.LevelContext;
import github.visual4.aacweb.dictation.korean.level.MarkFactory;

class DifficultyTest {
	LevelContext ctx;
	
	@BeforeEach
	public void initCtx() {
		ctx = new LevelContext(new MarkFactory());
	}
	
	@Test
	void test_L6_sen9() {
		String s = "건널목 건너서 학교 가요.";
		Mark mark = ctx.parseDifficulties(s);
		mark.each((df, pos) -> {
			System.out.printf("%s\n", df.desc);
			for(int k = 0 ; k < pos.size(); k++) {
				System.out.println(Arrays.toString(pos.rangeAt(k)));
			}
		});
	}
	
	@Test
	void test_max_L7_sen6() {
		String s = "줄넘기를 열심히 연습합니다.";
		Mark mark = ctx.parseDifficulties(s);
		mark.each((df, pos) -> {
			System.out.printf("%s\n", df.desc);
			for(int k = 0 ; k < pos.size(); k++) {
				System.out.println(Arrays.toString(pos.rangeAt(k)));
			}
		});
	}

}
