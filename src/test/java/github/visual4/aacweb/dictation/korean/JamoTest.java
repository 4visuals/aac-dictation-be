package github.visual4.aacweb.dictation.korean;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class JamoTest {

	@Test
	void test() {
		char song = '송';
		Jamo jamo = Jamo.decompose(song);
		/**
		 * 음소 bit-offset
		 * === =====
		 *  ㄱ 0  1<<0 := 1
		 *  ㄲ 1  1<<1 := 2
		 *  ㄴ 2  1<<2 := 4
		 *  ㄷ 3
		 *  ㄸ 4
		 *  ㄹ 5
		 *  ㅁ 6
		 *  ㅂ 7
		 *  ㅃ 8  
		 *  ㅅ 9  1<<9 := 512(0x0200)
		 *  ㅇ
		 *  ㅈ
		 *  ㅊ
		 *  ㅋ
		 * 
		 * 'ㅅ' 
		 */
		assertEquals(0x0200, jamo.cho);
		assertEquals(0x0100, jamo.jung);
		assertEquals(0x200000, jamo.jong);
	}
	
	@Test
	void test_패턴조립() {
		Jamo jm = Jamo.pattern("ㄱㄴㅎ", "ㅏㅓ", "ㅇㅂ");
		assertEquals(0x01 | (1 << 0x02) | (1 << 0x12), jm.cho);
		assertEquals(0x01 | (1 << 0x04), jm.jung);
		assertEquals((0x01<< 0x11) | (1 << 0x15), jm.jong);
	}
	
	@Test
	void test_decomposeKr() {
		String jamo = Jamo.decomposeKr("무ㄱㅎ회");
		System.out.println(jamo);
	}

}
