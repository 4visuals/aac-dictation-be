package github.visual4.aacweb.dictation.domain.assessment;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class AssessmentServiceTest {

	@Autowired
	AssessmentService service;
	@Test
	void test() {
		assertNotNull(service);
	}
	

	@Test
	void test28() {
		String question = "얼굴 그림을 그리는 것은 어려웠다.";
		List<String> answers = Arrays.asList(new String[]{
//				"얼굴 그림을 그리은 것은 어려웠다.",
//				"얼굴 그리믈 그리는 것은 어려웠다.",
//				"얼굴 그림를 그리는 것은 어려웠다.",
//				"얼굴 그림을 그리은 것 어려웠다.",
				"얼굴 그림을 그리는 그리는 것은 어려웠다.",
				"얼굴 그리는 그리는 것은 어려웠다.",
				"얼굴 그림는 그리는 것은 어려웠다.",
				"얼굴 어렴",
				"얼굴 그림는 어렴다."
		});
//		String question = "잡으러";
//		String answer = "자브러";
		boolean printLabel = true;
		for (String answer : answers) {
			Map<String, int[]> mark = service.mark(question, answer);
			print(printLabel, question, answer, mark);
			printLabel = false;
		}
	}
	void print(boolean label, String origin, String answer, Map<String, int[]> mark) {
		List<String> levels = new ArrayList<>(mark.keySet());
		
		levels.sort((a,b) -> a.compareTo(b));
		if(label) {
			System.out.printf("%s,%s",q("#"), q("LEVELS"));
			levels.forEach(lvl -> {
				System.out.printf(",%s", q(lvl));
			});
			System.out.println();
			System.out.printf("%s,%s",q("정답"), q(origin));
			levels.forEach(key ->{
				int [] v = mark.get(key);
				System.out.printf(",%d", v[1]);
			});
			System.out.println();
		}
		System.out.printf("%s,%s", q("오답"), q(answer));
		levels.forEach(key ->{
			int [] v = mark.get(key);
			System.out.printf(",%d", v[0]);
		});
		System.out.println();
	}
	private String q(String s) {
		return "\"" + s + "\"";
	}

}
