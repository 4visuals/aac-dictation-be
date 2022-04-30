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
		ctx = new LevelContext(new MarkFactory());
	}
	/**
	 * 자음(ㅇㄱㄴㄷㄹㅁㅂㅅㅈ) + 모음(ㅏㅓㅗㅜㅡㅣ) + 받침없음
	 */
	@Test
	void test_level1() {
		Level1 lvl = new Level1(ctx);
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
		
//		List<String> words = SampleReader.get(Difficulty.L1);
//		for (String word : words) {
//			mk = lvl.evaludate(word);
//			posData = mk.pos(Difficulty.L1);
//			assertEquals(1, posData.size());
//			assertArrayEquals(new int[] {0, word.length()}, posData.get(0).arr());
//		}
		
	}
	
	@Test
	void test_level2() {
		Level2 lvl = new Level2(ctx);
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
	void test_level3() {
		Level3 lvl = new Level3(ctx);
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
	public void test_level4() {
		Level4 lvl = new Level4(ctx);
		existing(lvl, Difficulty.L4);
	}
	@Test
	public void test_level5() {
		Level5 lvl = new Level5(ctx);
		existing(lvl, Difficulty.L5);	
	}
	@Test
	public void test_level6() {
		Level6 lvl = new Level6(ctx);
		existing(lvl, Difficulty.L6);
	}
	@Test
	public void test_level7() {
		Level7 lvl = new Level7(ctx);
		Mark mk = lvl.eval("갑니다");
		assertEquals(1, mk.pos(Difficulty.L7).size());
		assertArrayEquals(new int[] {0, 3}, mk.pos(Difficulty.L7).rangeAt(0));
		
		mk = lvl.eval("먹습니다.");
		assertEquals(1, mk.pos(Difficulty.L7).size());
		assertArrayEquals(new int[] {1, 4}, mk.pos(Difficulty.L7).rangeAt(0));
		
		existing(lvl, Difficulty.L7);
	}
	@Test
	public void test_level8() {
		Level8 lvl = new Level8(ctx);
		existing(lvl, Difficulty.L8);
	}
	@Test
	public void test_level9() {
		Level9 lvl = new Level9(ctx);
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
	public void test_level16() {
		Level16 lvl = new Level16(ctx);
		existing(lvl, Difficulty.L16);
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
		// FIXME 생략
//		Level41 lvl = new Level41(ctx);
//		existing(lvl, Difficulty.L41);
	}
	@Test
	public void test_level42() {
		Level42 lvl = new Level42(ctx);
		existing(lvl, Difficulty.L42);
	}
	@Test
	public void test_level43() {
		Level43 lvl = new Level43(ctx);
		existing(lvl, Difficulty.L43);
	}
	@Test
	public void test_level44() {
		Level44 lvl = new Level44(ctx);
		existing(lvl, Difficulty.L44);
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
