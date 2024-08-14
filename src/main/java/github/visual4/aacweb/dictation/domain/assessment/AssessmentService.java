package github.visual4.aacweb.dictation.domain.assessment;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Mark;
import github.visual4.aacweb.dictation.korean.Mark.Pos;
import github.visual4.aacweb.dictation.korean.level.LevelContext;
import github.visual4.aacweb.dictation.service.analysis.AnalysisService;

@Service

public class AssessmentService {

	final AnalysisService ansService;
	public AssessmentService(AnalysisService ansService) {
		this.ansService = ansService;
		
	}
	public Map<String, int[]> mark(String question, String answer) {
		
		TextDiff diff = TextDiff.build(question, answer);
		Map<String, int[]> scores = new HashMap<>();
		Mark mark = ansService.parseDifficulties(question);
		mark.eachLevel((Difficulty df, Pos poses) -> {
//			System.out.printf("%s\n", df);
			int [] score = {0, poses.size()};
			poses.each(pos -> {
				String txt = mark.textAt(pos[0], pos[1]);
				String ans = diff.getJamoAt(pos[0], pos[1]);
//				System.out.printf("[%d, %d] %s => %s\n",pos[0], pos[1], txt, ans );
				if(txt.equals(ans)) {
					score[0]++;
				}
			});
			scores.put(df.name(), score);
		});
		return scores;
	}
}
