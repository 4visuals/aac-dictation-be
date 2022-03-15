package github.visual4.aacweb.dictation.korean;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class JamoTest {

	@Test
	void test() {
		char song = '송';
		Jamo jamo = Jamo.decompose(song);
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

}
