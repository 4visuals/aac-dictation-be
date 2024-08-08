package github.visual4.aacweb.dictation.korean;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import github.visual4.aacweb.dictation.korean.level.Level01;
import github.visual4.aacweb.dictation.korean.level.Level02;
import github.visual4.aacweb.dictation.korean.level.Level05;
import github.visual4.aacweb.dictation.korean.level.Level08;
import github.visual4.aacweb.dictation.korean.level.Level18;
import github.visual4.aacweb.dictation.korean.level.LevelContext;

class JamoSetTest {

	LevelContext ctx;
	@BeforeEach
	public void ready() {
		ctx = new LevelContext();
	}
	@Test
	void test_L01() {
		Level01 lvl1 = new Level01(ctx);
		String word= "가나다";
		Mark mark = lvl1.eval(word);
		printMark(mark);
	}
	@Test
	void test_L18() {
		/**
		 * ㅂㅗ[ㄲㅇ]ㅡㅁ
		 * [2, 4]
		 */
		Level18 lvl = new Level18(ctx);
		String word= "볶음";
		Mark mark = lvl.eval(word);
		printMark(mark);
	}
	@Test
	void test_L01_L05() {
		Level01 lvl1 = new Level01(ctx);
		Level05 lvl5 = new Level05(ctx);
		String word= "강아지"; //L1 (아, 지), L2(강)
		Mark mark = lvl1.eval(word);
		mark = lvl5.eval(word);
		printMark(mark);
	}
	@Test
	void test_() {
		
		String word= "나눕니다"; //L1 (아, 지), L2(강)
		Mark mark = ctx.parseDifficulties(word);
		printMark(mark);
	}
	
	@Test
	void test() {
		JamoSet set = JamoSet.decompose("나눕니다");
		char [] chr = set.getJamo();
		System.out.println(new String(chr));
		
		LevelContext ctx = new LevelContext();
		Level08 lvl8 = new Level08(ctx);
		
//		lvl8.eval(ctx, set);
		Mark mk = ctx.mark(lvl8, set);
		printMark(mk);
	}
	void printMark(Mark mark) {
		mark.eachLevel((df, pos) -> {
			System.out.printf("%s\n", df.desc);
			for(int k = 0 ; k < pos.size(); k++) {
				int [] r = pos.rangeAt(k);
				System.out.println(Arrays.toString(pos.rangeAt(k)));
			}
		});
	}
}
