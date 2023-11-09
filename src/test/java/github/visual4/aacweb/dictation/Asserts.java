package github.visual4.aacweb.dictation;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.function.Predicate;

public class Asserts {

	public static <T> void asserts(Collection<T> elemes, Predicate<T> predicate) {
		for (T each : elemes) {
			assertTrue(predicate.test(each), () -> "fail to assert on " + each.toString());
		}
	}
	
	public static void notNulls(Object ... instances) {
		for (Object instance : instances) {
			assertNotNull(instance);
		}
	}
}
