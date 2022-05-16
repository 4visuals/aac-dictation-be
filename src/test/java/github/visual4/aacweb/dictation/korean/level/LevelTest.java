package github.visual4.aacweb.dictation.korean.level;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Mark;
import github.visual4.aacweb.dictation.korean.Mark.Pos;
/**
 * 각 글자가 단일 레벨에 해당함
 * ex) 트럭에
 *  3. '트' - 자음(ㅊㅋㅌㅍㅎ) + 모음에 해당
 *  6. '럭' - 받침 ㄱㄷㅂ에 해당
 *      => '럭'을 '벅'으로 잘못 썼을때 6레벨이 틀렸다고 판정해야 하는지?
 * 14. '에' - ㅔ와 ㅐ 구분하기에 해당
 * 
 * 하나의 글자가 둘 이상의 레벨에 해당함
 * ex) 완두콩 
 *  5. '완' - 받침 ㅇㄹㅁㄴ
 * 13. '완' - 모음ㅘ
 * 
 *  ex) 튤립
 * 
 * @author chminseo
 *
 */
class LevelTest {

	LevelContext ctx;
	
	@BeforeEach
	public void initCtx() {
		ctx = new LevelContext();
	}
	/**
	 * 자음(ㅇㄱㄴㄷㄹㅁㅂㅅㅈ) + 모음(ㅏㅓㅗㅜㅡㅣ) + 받침없음
	 */
	@Test
	void test_level01() {
		Level01 lvl = new Level01(ctx);
		Mark mk = lvl.eval("아기");
		Pos posData;
		assertTrue(mk.has(Difficulty.L1));
		assertArrayEquals(new int[] {0, 2}, mk.pos(Difficulty.L1).rangeAt(0));
		
		mk = lvl.eval("느티나무");
		posData = mk.pos(Difficulty.L1);
		assertEquals(2, posData.size());
		assertArrayEquals(new int[] {0, 1}, posData.rangeAt(0));
		assertArrayEquals(new int[] {2, 4}, posData.rangeAt(1));
		
		mk = lvl.eval("호주머니");
		posData = mk.pos(Difficulty.L1);
		assertEquals(1, posData.size());
		assertArrayEquals(new int[] {1, 4}, posData.rangeAt(0));
		
		mk = lvl.eval("나비야나비야");
		posData = mk.pos(Difficulty.L1);
		assertEquals(2, posData.size());
		assertArrayEquals(new int[] {0, 2}, posData.rangeAt(0));
		assertArrayEquals(new int[] {3, 5}, posData.rangeAt(1));
		
		existing(lvl, Difficulty.L1);
	}
	
	@Test
	void test_level02() {
		Level02 lvl = new Level02(ctx);
		Mark mk;
		
		mk = lvl.eval("아기");
		assertNull(mk.pos(Difficulty.L2));
		
		mk = lvl.eval("우유 마셔요.");
		Pos posData = mk.pos(Difficulty.L2);
		assertEquals(2, posData.size());
		
		existing(lvl, Difficulty.L2);
		// FIXME "여분" - '여'는 Level2가 맞음. '분'은 받침이 있어서 실패해야 하는지...?
		/*
		assertEquals(Difficulty.NONE, lvl.evaludate("여분"));
		
		assertEquals(Difficulty.L2, lvl.evaludate("우유"));
		assertEquals(Difficulty.L2, lvl.evaludate("마셔요"));
		assertEquals(Difficulty.L2, lvl.evaludate("마요네즈"));
		*/
	}
	
	@Test
	void test_level03() {
		Level03 lvl = new Level03(ctx);
		
		System.out.println(lvl);
		assertNull(lvl.eval("아기").pos(Difficulty.L3));
		assertNull(lvl.eval("우유").pos(Difficulty.L3));
		// FIXME "휴먼" : 받침이 있으므로 Level 3에 해당하지 않는지...?
//		assertNull(lvl.evaludate("휴먼").pos(Difficulty.L3));
		// 받침이 있으므로 L3에 해당하지 않음
//		assertNull(lvl.evaludate("하늘").pos(Difficulty.L3));
		
		assertArrayEquals(new int[] {0, 1}, lvl.eval("피아노")
				.pos(Difficulty.L3)
				.rangeAt(0));
		assertArrayEquals(new int[] {1, 2}, lvl.eval("아파요").pos(Difficulty.L3).rangeAt(0));
		assertArrayEquals(new int[] {1, 2}, lvl.eval("구토").pos(Difficulty.L3).rangeAt(0));
		assertArrayEquals(new int[] {0, 1}, lvl.eval("하나").pos(Difficulty.L3).rangeAt(0));
		
		existing(lvl, Difficulty.L3);
	}
	
	@Test
	public void test_level04() {
		Level04 lvl = new Level04(ctx);
		existing(lvl, Difficulty.L4);
	}
	@Test
	public void test_level05() {
		Level05 lvl = new Level05(ctx);
		existing(lvl, Difficulty.L5);	
	}
	/**
	 * 6. ~다.
	 */
	@Test
	public void test_level06() {
		Level06 lvl = new Level06(ctx);
		existing(lvl, Difficulty.L6);
	}
	@Test
	public void test_level07() {
		Level07 lvl = new Level07(ctx);
		existing(lvl, Difficulty.L7);
	}
	@Test
	public void test_level08() {
		Level08 lvl = new Level08(ctx);
		existing(lvl, Difficulty.L8);
	}
	@Test
	public void test_level09() {
		Level09 lvl = new Level09(ctx);
		existing(lvl, Difficulty.L9);
	}
	@Test
	public void test_level10() {
		Level10 lvl = new Level10(ctx);
		existing(lvl, Difficulty.L10);
	}
	@Test
	public void test_level11() {
		Level11 lvl = new Level11(ctx);
		existing(lvl, Difficulty.L11);
	}
	@Test
	public void test_level12() {
		Level12 lvl = new Level12(ctx);
		existing(lvl, Difficulty.L12);
	}
	@Test
	public void test_level13() {
		Level13 lvl = new Level13(ctx);
		existing(lvl, Difficulty.L13);
	}
	@Test
	public void test_level14() {
		Level14 lvl = new Level14(ctx);
		existing(lvl, Difficulty.L14);
	}
	@Test
	public void test_level15() {
		Level15 lvl = new Level15(ctx);
		existing(lvl, Difficulty.L15);
	}
	@Test
	public void test_level17() {
		Level17 lvl = new Level17(ctx);
		existing(lvl, Difficulty.L17);
	}
	@Test
	public void test_level18() {
		Level18 lvl = new Level18(ctx);
		existing(lvl, Difficulty.L18);
	}
	@Test
	public void test_level19() {
		Level19 lvl = new Level19(ctx);
		existing(lvl, Difficulty.L19);
	}
	@Test
	public void test_level20() {
		Level20 lvl = new Level20(ctx);
		existing(lvl, Difficulty.L20);
	}
	@Test
	public void test_level21() {
		Level21 lvl = new Level21(ctx);
		existing(lvl, Difficulty.L21);
	}
	@Test
	public void test_level22() {
		Level22 lvl = new Level22(ctx);
		existing(lvl, Difficulty.L22);
	}
	@Test
	public void test_level23() {
		Level23 lvl = new Level23(ctx);
		existing(lvl, Difficulty.L23);
	}
	@Test
	public void test_level24() {
		Level24 lvl = new Level24(ctx);
		existing(lvl, Difficulty.L24);
	}
	@Test
	public void test_level25() {
		Level25 lvl = new Level25(ctx);
		existing(lvl, Difficulty.L25);
	}
	@Test
	public void test_level26() {
		Level26 lvl = new Level26(ctx);
		existing(lvl, Difficulty.L26);
	}
	@Test
	public void test_level27() {
		Level27 lvl = new Level27(ctx);
		existing(lvl, Difficulty.L27);
	}
	@Test
	public void test_level28() {
		Level28 lvl = new Level28(ctx);
		lvl.eval("언니와 나는 가위바위보를 했습니다.");
		existing(lvl, Difficulty.L28);
	}
	@Test
	public void test_level29() {
		Level29 lvl = new Level29(ctx);
		existing(lvl, Difficulty.L29);
	}
	@Test
	public void test_level30() {
		Level30 lvl = new Level30(ctx);
		existing(lvl, Difficulty.L30);
	}
	@Test
	public void test_level31() {
		Level31 lvl = new Level31(ctx);
		existing(lvl, Difficulty.L31);
	}
	@Test
	public void test_level32() {
		Level32 lvl = new Level32(ctx);
		existing(lvl, Difficulty.L32);
	}
	@Test
	public void test_level33() {
		Level33 lvl = new Level33(ctx);
		existing(lvl, Difficulty.L33);
	}
	@Test
	public void test_level34() {
		Level34 lvl = new Level34(ctx);
		existing(lvl, Difficulty.L34);
	}
	@Test
	public void test_level35() {
		Level35 lvl = new Level35(ctx);
		existing(lvl, Difficulty.L35);
	}
	@Test
	public void test_level36() {
		Level36 lvl = new Level36(ctx);
		existing(lvl, Difficulty.L36);
	}
	@Test
	public void test_level37() {
		Level37 lvl = new Level37(ctx);
		existing(lvl, Difficulty.L37);
	}
	@Test
	public void test_level38() {
		Level38 lvl = new Level38(ctx);
		existing(lvl, Difficulty.L38);
	}
	@Test
	public void test_level39() {
		Level39 lvl = new Level39(ctx);
		existing(lvl, Difficulty.L39);
	}
	@Test
	public void test_level40() {
		Level40 lvl = new Level40(ctx);
		existing(lvl, Difficulty.L40);
	}
	@Test
	public void test_level41() {
		Level41 lvl = new Level41(ctx);
		existing(lvl, Difficulty.L41);
	}
	@Test
	public void test_level42() {
		Level42 lvl = new Level42(ctx);
		existing(lvl, Difficulty.L42);
	}
	@Test
	public void test_level43() {
		// FIXME 43. 글자와 뜻이 달라요1 
	}
	@Test
	public void test_level44() {
		// FIXME 44. 글자와 뜻이 달라요2 
	}
	@Test
	public void test_level45() {
		// FIXME 45. 글자와 뜻이 달라요3 
	}
	@Test
	public void test_level46() {
		Level46 lvl = new Level46(ctx);
		existing(lvl, Difficulty.L46);
	}
	@Test
	public void test_level47() {
		Level47 lvl = new Level47(ctx);
		existing(lvl, Difficulty.L47);
	}
	@Test
	public void test_level48() {
		Level48 lvl = new Level48(ctx);
		existing(lvl, Difficulty.L48);
	}
	
	@Test
	public void test_level49() {
//		Level49 lvl = new Level49(ctx);
//		existing(lvl, Difficulty.L49);
	}
	@Test
	public void test_level50() {
//		Level50 lvl = new Level50(ctx);
//		existing(lvl, Difficulty.L50);
	}
	void existing(ILevel lvl, Difficulty df) {
		List<String> words = SampleReader.get(df, "sample.txt", ",");
		for (String word : words) {
			Mark mk = lvl.eval(word);
			Pos posData = mk.pos(df);
			assertNotNull(posData, "null Pos for word " + word);
			assertTrue(posData.size() > 0);
		}
	}
}
