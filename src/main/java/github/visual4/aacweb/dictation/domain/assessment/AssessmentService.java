package github.visual4.aacweb.dictation.domain.assessment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import github.visual4.aacweb.dictation.domain.assessment.TextDiff.Cause;
import github.visual4.aacweb.dictation.domain.assessment.TextDiff.Mapping;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
import github.visual4.aacweb.dictation.korean.level.LevelContext;

public class AssessmentService {

	public Map<String, int[]> mark(String question, String answer) {
		
		TextDiff diff = TextDiff.build(question, answer);
		
		List<Mapping> answerMap = new ArrayList<>();
		List<Mapping> mappings = diff.getMappings();
		int offset = 0;
		
		int k = 0;
		while(k < diff.getMappings().size()) {
			Mapping m = mappings.get(k);
			
			if (m.cause == Cause.SAME) {
				String org = Jamo.decomposeKr(question.substring(m.charIdx, m.charIdx + 1));
				for(int j = 0 ; j < org.length(); j++) {
					answerMap.add(Mapping.same(3 * m.charIdx + j , offset));	
				}
				k++;
			} else if (m.cause == Cause.CHANGE) {
				int idx = m.charIdx;
				String org = Jamo.decomposeKr(question.substring(idx, idx + 1));
				String ans = Jamo.decomposeKr(answer.substring(idx + m.offset, idx + m.offset + 1));
				for(int j = 0 ; j < org.length(); j++) {
					if(org.charAt(j) == ans.charAt(j)) {
						answerMap.add(Mapping.same(3 * m.charIdx + j, offset));
					} else {
						answerMap.add(Mapping.change(3 * m.charIdx + j, offset));
					}
				}
				k++;
			} else if (m.cause == Cause.DEL) {
//				offset -= 3;
				for(int j = 0 ; j < 3; j++) {
					answerMap.add(Mapping.deleted(3 * m.charIdx + j, offset--));
				}
				k++;
			} else if (m.cause == Cause.ADD) {
				offset += 3;
				k++;
			}
		}
		TextDiff diff2 = new TextDiff(Jamo.decomposeKr(question), Jamo.decomposeKr(answer), answerMap);
		
		LevelContext ctx = new LevelContext();
		Map<String, int[]> scores = new HashMap<>();
		Mark mark = ctx.parseDifficulties(question);
		mark.eachLevel((df, posList) -> {
			System.out.printf("%s\n", df);
			int [] score = {0, posList.size()};
			posList.each(pos -> {
				String txt = mark.textAt(pos[0], pos[1]);
				int [] rev = diff2.transformPos(pos[0], pos[1]-1);
//				String ans = mark.textAt(rev[0], rev[1]);
				String ans = diff2.getReviseText(pos[0], pos[1] - 1);
				System.out.printf("%s[%d, %d] => ", txt, pos[0], pos[1] );
				System.out.printf("%s[%d, %d]\n", ans, rev[0], rev[1] );
				if(txt.equals(ans)) {
					score[0] ++;
				}
			});
			scores.put(df.name(), score);
		});
		return scores;
	}
}
