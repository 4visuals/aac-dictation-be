package github.visual4.aacweb.dictation.domain.exam;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import github.visual4.aacweb.dictation.Res;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.exam.recent.RecentPaper;
import github.visual4.aacweb.dictation.domain.exam.recent.RecentPaperService;
import github.visual4.aacweb.dictation.domain.sentence.Sentence.SentenceType;
import github.visual4.aacweb.dictation.domain.user.UserRole;
import github.visual4.aacweb.dictation.web.aop.JwtProp;

@RestController
@RequestMapping("/api")
public class ExamController {

	final ExamService examService;
	final RecentPaperService recentPaperService;
	
	public ExamController(ExamService examService, RecentPaperService recentPaperService) {
		this.examService = examService;
		this.recentPaperService = recentPaperService;
	}
	/**
	 * 주어진 section의 제출 답안 조회. section별 정답, 오답 입력까지 모두 반환함
	 * @param sectionSeq - section pk
	 * @param mode - R:보고쓰기, L:학습, Q:퀴즈
	 * @param type - W:낱말, S:문장
	 * @param license
	 * @return
	 */
	@GetMapping("/answer/section/{sectionSeq}/{mode}/{type}")
	public Object findExamofSectionByLicense(
			@PathVariable Integer sectionSeq,
			@PathVariable ExamMode mode,
			@PathVariable SentenceType type,
			@RequestParam String license) {
		Object papers = null;
		/* --------+--------+-------+----------+
		 *         | 보고쓰기 |  학습  |  퀴즈(Q)  |
		 * --------+--------+-------+----------+
		 *  낱말(W) |   EP   |   EP  |   EP     |
		 * --------+--------+-------+----------+
		 *  문장(S) |   LP   |   LP  |   EP     |
		 * --------+--------+-------+----------+
		 * 
		 * 문장의 경우 보고쓰기와 학습의 경우 어절 단위로 입력을 받는다. LearningPaper(LP)
		 * 문장에 대한 퀴즈모드에서는 전체 문장을 통째로 입력 받는다. ExamPaper(EP)
		 * 낱말의 경우 전체 낱말을 통째로 입력받으므로 ExamMode의 구분이 없음. 모두 EP로 입력받음
		 */
		if (mode == ExamMode.Q || type == SentenceType.W) {
			papers = examService.findExamPapersBySection(sectionSeq, type, license);
		} else {
			papers = examService.findLearningPapersBySection(sectionSeq, license);
		}
		return Res.success("papers", papers);
	}
	/**
	 * 주어진 섹션의 틀린 문제들
	 * @return
	 */
	@GetMapping("/wrong/student/{studentSeq}/section/{sectionSeq}")
	public Object findWrongAnswersBySection(
			@JwtProp("useq") Integer teacherSeq,
			@PathVariable Integer studentSeq,
			@PathVariable Integer sectionSeq
			) {
		List<RecentPaper> papers = recentPaperService.findWrongAnswers(
				studentSeq.longValue(),
				sectionSeq);
		return Res.success("papers", papers);
	}
	@GetMapping("/records/student/{studentSeq}")
	public Object findSectionRecordsByStudent(
			@JwtProp("useq") Integer teacherSeq,
			@PathVariable Integer studentSeq) {
		List<RecentPaper> papers = recentPaperService.findWrongAnswersByStudent(
				teacherSeq.longValue(),
				studentSeq.longValue()
				);
		return Res.success("papers", papers);
	}
	/**
	 * 시험 결과 입력 - 화면에서 문장퀴즈, 낱말퀴즈, 종합에 해당. 문장에 대한 입력값으로 처리함.
	 * @param exam
	 * @return
	 */
	@PostMapping("/exam/quiz")
	public Object submit(@JwtProp("useq") Integer studentSeq, @RequestBody ExamPaper exam) {
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
	public Object submitLearning(@RequestBody LearningPaper eojelPaper) {
		examService.insertLearningPaper(eojelPaper);
		return Res.success(true);
	}
	/**
	 * 학생의 단계별 학습 이력(보고쓰기, 학습, 퀴즈 구분없이 전부 반환)
	 * @return
	 */
	@GetMapping("/exams")
	public Object queryStudentExams(
			@JwtProp("aac_id") String userId,
			@JwtProp("role") UserRole role,
			@RequestParam Map<String, Object> params) {
		String license = (String) params.get("license");
		System.out.println(userId + " role: " + role);
		System.out.println(params);
		TypeMap exams = null;
		if (role == UserRole.STUDENT) {
			exams = examService.findExamsByLicense(userId, license );
		} else {
			exams = examService.findExamsByStudentLicense(userId, license);
		}
		TypeMap segment = examService.queryBySectionChunk(license);
		exams.put("segments", segment.get("quiz"));
		return Res.success(exams);
	}
	/**
	 * 통계용.
	 * section의 segment마다 가장 최근의 받아쓰기(mode: 'QUIZ')만 조회함
	 * @param userId
	 * @param role
	 * @param params
	 * @return
	 */
	@GetMapping("/exams/segments")
	public Object queryBySectionChunk(
			@JwtProp("aac_id") String userId,
			@JwtProp("role") UserRole role,
			@RequestParam Map<String, Object> params) {
		String license = (String) params.get("license");
		TypeMap res = examService.queryBySectionChunk(license);
		return Res.success(res);
	}
}
