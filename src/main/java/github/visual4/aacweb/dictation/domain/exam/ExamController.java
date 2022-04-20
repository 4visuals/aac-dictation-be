package github.visual4.aacweb.dictation.domain.exam;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.visual4.aacweb.dictation.Res;
import github.visual4.aacweb.dictation.TypeMap;

@RestController
@RequestMapping("/api")
public class ExamController {

	final ExamService examService;
	
	public ExamController(ExamService examService) {
		super();
		this.examService = examService;
	}
	/**
	 * 시험 결과 - 화면에서 문장퀴즈, 낱말퀴즈, 종합에 해당. 문장에 대한 입력값으로 처리함.
	 * @param exam
	 * @return
	 */
	@PostMapping("/exam/quiz")
	public Object submit(@RequestBody ExamPaper exam) {
		/*
		 * {license=lcs-5ef9b51e-f501-4df3-aa88-f5bbbfce6e5a,
		 *   submissions=[
		 *     {type=sen, correct=false, value=어자, elapsedTime=1902, eojeolRef=8256, sentenceRef=643, trialCnt=2},
		 *     {type=sen, correct=true, value=여자, elapsedTime=585, eojeolRef=8256, sentenceRef=643, trialCnt=2},
		 *     {type=sen, correct=false, value=겨울, elapsedTime=690, eojeolRef=3840, sentenceRef=644, trialCnt=3},
		 *     {type=sen, correct=false, value=거울, elapsedTime=987, eojeolRef=3840, sentenceRef=644, trialCnt=3},
		 *     {type=sen, correct=true, value=유리, elapsedTime=784, eojeolRef=3840, sentenceRef=644, trialCnt=3},
		 *     {type=sen, correct=true, value=우유, elapsedTime=729, eojeolRef=131, sentenceRef=645, trialCnt=1}],
		 *  section=2
		 * }
		 */
		System.out.println(exam);
		examService.insertExamPaper(exam);
		return Res.success("exam", exam);
	}
	/**
	 * 연습 결과 - 화면에서 문장쓰기, 낱말쓰기에 해당. 어절에 대한 입력값으로 처리함
	 * @return
	 */
	@PostMapping("/exam/learning")
	public Object submitLearning(@RequestBody EojeolPaper eojelPaper) {
		examService.insertEojeolPaper(eojelPaper);
		return Res.success(true);
	}
}
