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
		ctx = new LevelContext();
	}
	@Test
	void test_L1() {
		String s = "기차 타고 떠나요.";
		Mark mark = ctx.parseDifficulties(s);
		mark.eachLevel((df, pos) -> {
			System.out.printf("%s\n", df.desc);
			for(int k = 0 ; k < pos.size(); k++) {
				System.out.println(Arrays.toString(pos.rangeAt(k)));
			}
		});
	}
	@Test
	void test_L5() {
		String s = "힘들다";
		Mark mark = ctx.parseDifficulties(s);
		mark.eachLevel((df, pos) -> {
			System.out.printf("%s\n", df.desc);
			for(int k = 0 ; k < pos.size(); k++) {
				System.out.println(Arrays.toString(pos.rangeAt(k)));
			}
		});
	}
	
	@Test
	void test_L6_sen9() {
		String s = "건널목 건너서 학교 가요.";
		Mark mark = ctx.parseDifficulties(s);
		mark.eachLevel((df, pos) -> {
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
		mark.eachLevel((df, pos) -> {
			System.out.printf("%s\n", df.desc);
			for(int k = 0 ; k < pos.size(); k++) {
				System.out.println(Arrays.toString(pos.rangeAt(k)));
			}
		});
	}
	@Test
	void test_L5_L33() {
		/*
		 * ㅁㅓㄱㅅㅡㅂㄴㅣ_ㄷㅏ_
		 */
		String s = "먹습니다";
		JamoSet jamo = JamoSet.decompose(s);
		Mark mark = ctx.parseDifficulties(s);
		mark.eachLevel((df, pos) -> {
			System.out.printf("%s\n", df.desc);
			for(int k = 0 ; k < pos.size(); k++) {
				System.out.println(Arrays.toString(pos.rangeAt(k)));
			}
		});
	}

}
