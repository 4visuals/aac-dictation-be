package github.visual4.aacweb.dictation.domain.exam;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import github.visual4.aacweb.dictation.Res;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.sentence.Sentence.SentenceType;

@RestController
@RequestMapping("/api")
public class ExamController {

	final ExamService examService;
	
	public ExamController(ExamService examService) {
		super();
		this.examService = examService;
	}
	/**
	 * 주어진 section의 시험결과(quiz) 조회. section별 정답, 오답 입력까지 모두 반환함
	 * @param sectionSeq
	 * @param license
	 * @return
	 */
	@GetMapping("/exam/quiz/section/{sectionSeq}")
	public Object findExamofSectionByLicense(
			@PathVariable Integer sectionSeq,
			@RequestParam SentenceType sentenceType,
			@RequestParam String license) {
		List<ExamPaper> papers = examService.findExamofSectionByLicense(sectionSeq, sentenceType, license);
		return Res.success("papers", papers);
	}
	/**
	 * 시험 결과 입력 - 화면에서 문장퀴즈, 낱말퀴즈, 종합에 해당. 문장에 대한 입력값으로 처리함.
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
