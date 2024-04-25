package github.visual4.aacweb.dictation.domain.order.delivery;

import static org.junit.jupiter.api.Assertions.*;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

class DeliveryInfoTest {

	@Test
	void test() {
		assertTrue(Pattern.matches("^\\d{2,3}$", "020"));
		assertTrue(Pattern.matches("^\\d{2,3}$", "010"));
		assertTrue(Pattern.matches("^\\d{2,3}$", "02"));
		assertFalse(Pattern.matches("^\\d{2,3}$", "0a"));
		
		assertTrue(Pattern.matches("^\\d{3,4}$", "1231"));
		assertTrue(Pattern.matches("^\\d{3,4}$", "772"));
	}

}
