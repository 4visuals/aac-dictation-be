package github.visual4.aacweb.dictation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class QueryTest {

	@Test
	void test() {
		String q = new Query("select @col from user")
				.bind("@col", "a, b, c").get();
		System.out.println(q);
	}

}
