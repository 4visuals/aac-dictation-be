package github.visual4.aacweb.dictation.domain.assessment;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.diff.CommandVisitor;
import org.apache.commons.text.diff.EditScript;
import org.apache.commons.text.diff.StringsComparator;
import org.junit.jupiter.api.Test;

import github.visual4.aacweb.dictation.korean.Jamo;

class TextDiffTest {

	@Test
	void whenEditScript_thenCorrect() {
	    StringsComparator cmp = new StringsComparator("puublic", "Pblik");
	    EditScript<Character> script = cmp.getScript();
	    int mod = script.getModifications();
	    
	    final int[] qIndex = {0};
        final int[] aIndex = {0};
        
	    
	    script.visit(new CommandVisitor<Character>() {
	    	@Override
            public void visitKeepCommand(Character object) {
                System.out.println("[ ] " + object + " (Oi: " + qIndex[0] + ", Ri: " + aIndex[0] + ")");
                qIndex[0]++;
                aIndex[0]++;
            }
            @Override
            public void visitInsertCommand(Character object) {
                System.out.println("[+] " + object + " (Oi: " + qIndex[0] + " Ri: " + aIndex[0] + ")");
                aIndex[0]++;
            }
            @Override
            public void visitDeleteCommand(Character object) {
                System.out.println("[-] " + object + " (Oi: " + qIndex[0] + " Ri: " + aIndex[0] + ")");
                qIndex[0]++;
            }
		});
//	    assertEquals(3, mod);
	}
	@Test
	void dff1() {
		String question = "잡으러";
		
		String answer = "자브으러";
		TextDiff diff = TextDiff.build(question, answer);
		System.out.println(diff);
		
		diff = TextDiff.build("잡으러", "자로");
		System.out.println(diff);
		String jamo = Jamo.decomposeKr("잡으러");
		System.out.println(jamo);
	}
	@Test
	void diff() {		
		AssessmentService service = new AssessmentService(null);
		
		String question = "풀을 뜯는 젖소들";
		List<String> answers = Arrays.asList(new String[]{
				"푸를 뜯는 덪소들",
				"풀을뜯는 젖소들", // 공백 탈락
				"풀을 뜬는 젓소들",
				"풀을 뜯으는 젖소들",
				"풀을 뜯는 젖들",
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
	@Test
	void diff28() {		
		AssessmentService service = new AssessmentService(null);
		
		String question = "원숭이와 토끼는 치마를 입고 춤을 췄다.";
		List<String> answers = Arrays.asList(new String[]{
				"원숭이와 토끼는 치마를 입고 춤을 췄다.",
				"원숭이와토끼는치마를입고춤을췄다.", // 공백 탈락
				"원 숭 이 와 토 끼 는 치 마 를 입 고 춤 을 췄 다.",
				"원숭이과 토끼가 치마 입고 춤 췄다.",
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
