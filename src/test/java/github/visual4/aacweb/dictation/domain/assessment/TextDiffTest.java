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
	void diff() {
//		List<TextDiff.Mapping> mappings = TextDiff.build("ㅂㅜ_ㅅㅏㄴ", "ㅁㅜㄴㅅㅏㄴ");
		// 물회 => 무래 (종성의 ㄹ이 초성의 ㄹ과 매칭됨)
		// 간호사 => 가노사
		// 어두워요 => 어둥워요, 어두어요
		// 책을 넣으니 가방 => 책을 넣어 가방
		// 책을 넣어 가방
		// ㅁ ㅜ ㄹ ㅎ ㅚ _
		// ㅁ ㅜ _ ㄹ ㅐ _
//		String origin = "책을 넣으니 가방";
//		String answer = "책을 넣어 가방"; // "어둥워요";
		
		AssessmentService service = new AssessmentService();
//		Map<String, int[]> mark = service.mark("넣어서", "넣고");
		
		String question = "풀을 뜯는 젖소들";
		String answer = "풀을 뜯는 덪소들";
		Map<String, int[]> mark = service.mark(question, answer);
		print(question, answer, mark);
		
	}
	void print(String origin, String answer, Map<String, int[]> mark) {
		List<String> levels = new ArrayList<>(mark.keySet());
		
		levels.sort((a,b) -> a.compareTo(b));
		
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
