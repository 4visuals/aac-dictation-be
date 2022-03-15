package github.visual4.aacweb.dictation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class KorUtilTest {

	@Test
	void test() {
		char c = '삯';
		String[] chr = KoUtil.decompose(c);
		System.out.println(Arrays.toString(chr));
		assertArrayEquals("ㅅㅏㄳ".split(""), chr);
		
		chr = KoUtil.decompose('쬻');
		System.out.println(Arrays.toString(chr));
		assertArrayEquals("ㅉㅛㅀ".split(""), chr);
		
	}
	
	@Test
	void test_moeum() {
		// 'ㅑ'
		int m = 0x1161;
		for(int offset = 0; offset < 150; offset++) {
			System.out.printf("%2d: %s(%d)\n", offset, (char)(m+offset), (m+offset));
		}
	}
	
	@Test
	void test_codes() {
		String [] codes = KoUtil.toCodes('강');
		System.out.println(Arrays.toString(codes));
		
		int [] hexCodes = {0x1100, 0x1161, 0x11bc};
		System.out.println(KoUtil.compose(hexCodes));
		
		hexCodes = new int[] {0x110c, 0x1161, 0x11ab};
		System.out.println(KoUtil.compose(hexCodes));
		
		int k = 1;
		System.out.println(k<<0);
	}

}
