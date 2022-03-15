package github.visual4.aacweb.dictation.korean.level;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import github.visual4.aacweb.dictation.korean.Difficulty;
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

	/**
	 * 자음(ㅇㄱㄴㄷㄹㅁㅂㅅㅈ) + 모음(ㅏㅓㅗㅜㅡㅣ) + 받침없음
	 */
	@Test
	void test_level1() {
		Level1 lvl = new Level1();
		assertEquals(Difficulty.L1, lvl.evaludate("아기"));
		assertEquals(Difficulty.L1, lvl.evaludate("오소리"));
		assertEquals(Difficulty.L1, lvl.evaludate("나비"));
		
		assertEquals(Difficulty.NONE, lvl.evaludate("우유"));
		assertEquals(Difficulty.NONE, lvl.evaludate("나비야"));
		
	}
	
	@Test
	void test_level2() {
		Level2 lvl = new Level2();
		assertEquals(Difficulty.NONE, lvl.evaludate("아기"));
		// FIXME "여분" - '여'는 Level2가 맞음. '분'은 받침이 있어서 실패해야 하는지...?
		assertEquals(Difficulty.NONE, lvl.evaludate("여분"));
		
		assertEquals(Difficulty.L2, lvl.evaludate("우유"));
		assertEquals(Difficulty.L2, lvl.evaludate("마셔요"));
		assertEquals(Difficulty.L2, lvl.evaludate("마요네즈"));
	}
	
	@Test
	void test_level3() {
		Level3 lvl = new Level3();
		assertEquals(Difficulty.NONE, lvl.evaludate("아기"));
		assertEquals(Difficulty.NONE, lvl.evaludate("우유"));
		// FIXME "휴먼" : 받침이 있으므로 Level 3에 해당하지 않는지...?
		assertEquals(Difficulty.NONE, lvl.evaludate("휴먼"));
		
		assertEquals(Difficulty.L3, lvl.evaludate("피아노"));
		assertEquals(Difficulty.L3, lvl.evaludate("아파요"));
		assertEquals(Difficulty.L3, lvl.evaludate("구토"));
		assertEquals(Difficulty.L3, lvl.evaludate("하늘"));
	}

}
