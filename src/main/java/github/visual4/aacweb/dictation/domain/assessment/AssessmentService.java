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
	/**
	 * 질문과 답변으로 채점 결과를 반환함
	 * @param question
	 * @param answer
	 * @return
	 */
	public Map<String, int[]> mark(String question, String answer) {
		/*
		 *  FIXME 띄어쓰기 오답, 기호 오답을 level에 포함해야 함
		 *  L51 - 띄어쓰기 탈락(띄어써야 하는데 붙여씀)
		 *  L52 - 띄어쓰기 추가(붙여써야 하는데 띄어씀)
		 *  L52 - 기호
		 */
		TextDiff diff = TextDiff.build(question, answer);
		Map<String, int[]> scores = new HashMap<>();
		Mark mark = ansService.parseDifficulties(question);
		mark.eachLevel((Difficulty df, Pos poses) -> {
			int [] score = {0, poses.size()};
			poses.each(pos -> {
				String txt = mark.textAt(pos[0], pos[1]);
				String ans = diff.getJamoAt(pos[0], pos[1]);
				if(txt.equals(ans)) {
					score[0]++;
				}
			});
			scores.put(df.name(), score);
		});
		return scores;
	}
}
