package github.visual4.aacweb.dictation.domain.exam;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.Util;
import github.visual4.aacweb.dictation.domain.assessment.AssessmentService;
import github.visual4.aacweb.dictation.domain.exam.dto.PaperQuery;
import github.visual4.aacweb.dictation.domain.exam.recent.RecentPaperService;
import github.visual4.aacweb.dictation.domain.license.License;
import github.visual4.aacweb.dictation.domain.license.LicenseService;
import github.visual4.aacweb.dictation.domain.section.Section;
import github.visual4.aacweb.dictation.domain.section.SectionService;
import github.visual4.aacweb.dictation.domain.sentence.Sentence;
import github.visual4.aacweb.dictation.domain.sentence.Sentence.SentenceType;
import github.visual4.aacweb.dictation.domain.user.User;
import github.visual4.aacweb.dictation.domain.user.UserService;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class ExamService {
	final ObjectMapper om;
	final LicenseService licenseService;
	final UserService userService;
	final ExamPaperDao examPaperDao;
	final LearningPaperDao learningPaperDao;
	final ExamAnswerDao examAnswerDao;
	final EojeolAnswerDao eojeolAnswerDao;
	final SectionService sectionService;
	final RecentPaperService recentPaperService;
	final AssessmentService assessmentService;

	public ExamService(
			ObjectMapper om,
			LicenseService licenseService,
			UserService userService,
			ExamPaperDao examPaperDao,
			LearningPaperDao learningPaperDao,
			ExamAnswerDao examAnswerDao,
			EojeolAnswerDao eojeolAnswerDao,
			SectionService sectionService,
			RecentPaperService recentPaperService,
			AssessmentService assessmentService) {
		super();
		this.om = om;
		this.licenseService = licenseService;
		this.userService = userService;
		this.examPaperDao = examPaperDao;
		this.learningPaperDao = learningPaperDao;
		this.examAnswerDao = examAnswerDao;
		this.eojeolAnswerDao = eojeolAnswerDao;
		this.sectionService = sectionService;
		this.recentPaperService = recentPaperService;
		this.assessmentService = assessmentService;
	}

	/**
	 * 낱말퀴즈, 문장퀴즈 제출 답안. 각 문장에 대한 답안이 작성됨
	 * 시험대상 - 낱말퀴즈, 문장퀴즈, 종합테스트에 해당함
	 * 
	 * @param examPaper
	 */
	public void insertExamPaper(ExamPaper examPaper) {
		// 재시도가 아닌 경우만 시험 이력을 기록함
		String licenseUuid = examPaper.getLicense();
		License license = licenseService.findBy(License.Column.lcs_uuid, licenseUuid, true);
		User student = licenseToStudent(license);
		User teacher = userService.findTeacher(student.getTeacherRef());
		if (!teacher.isLicenseOwner(license)) {
			throw new AppException(ErrorCode.NOT_A_LICENSE_OWNER, 422);
		}
		Section section = sectionService.findBy(Section.Column.seq, examPaper.getSectionRef());
		if (section== null) {
			throw new AppException(ErrorCode.NOT_FOUND, 400, "section: " + examPaper.getSectionRef());	
		}
		examPaper.setStudentRef(student.getSeq());
		examPaper.setAgeInMonth(student.getAgeInMonth(Instant.now()));
		examPaper.setOrigin(section.getOrigin());
	
		examPaperDao.insertExamPaper(examPaper);
		
		examPaper.eachAnswer(answer -> {
			answer.setExamRef(examPaper.getSeq());
			examAnswerDao.insertExamAnswer(answer);
		});
		if (examPaper.getMode() == ExamMode.Q && !examPaper.isRetry()) {
			// 받아쓰기일때만 최신 이력을 입력함
			// section마다 틀린 문제를 추적함
			 recentPaperService.insertRecentPaper(examPaper);			
		}
	}
	/**
	 * 낱말학습, 문장학습 제출 답안. 각 문장의 어절에 대해 답안이 작성됨
	 * 
	 * @param eojelPaper
	 */
	public void insertLearningPaper(LearningPaper learningPaper) {
		String uuid = learningPaper.getLicense();
		License license = licenseService.findBy(License.Column.lcs_uuid, uuid, true);
		User student = licenseToStudent(license);
		User teacher = userService.findTeacher(student.getTeacherRef());
		if (!teacher.isLicenseOwner(license)) {
			throw new AppException(ErrorCode.NOT_A_LICENSE_OWNER, 422);
		}
		learningPaper.setStudentRef(student.getSeq());
		learningPaper.setAgeInMonth(student.getAgeInMonth(Instant.now()));
		
		learningPaperDao.insertLearningPaper(learningPaper);
		
		learningPaper.forEachAnswer(answer -> {
			answer.setExamRef(learningPaper.getSeq());
			answer.setAgeInMonth(learningPaper.getAgeInMonth());
			answer.setStudentRef(student.getSeq());
		});
		eojeolAnswerDao.insertAnswer(learningPaper.getSubmissions());
	}
	/**
	 * 문장 기반 시험 문제 결과. 라이선스에 연결된 학생이 제출한 section의 답안지 상세 정보(문제 및 정오답 모두 포함)
	 * @param sectionSeq
	 * @param type
	 * @para licenseUuid
	 * @return
	 */
	public List<ExamPaper> findExamPapersBySection(
			Integer sectionSeq,
			SentenceType type,
			String licenseUuid) {
		License license = licenseService.findBy(License.Column.lcs_uuid, licenseUuid, true);
		User student = licenseToStudent(license);
		List<ExamPaper> papers = examPaperDao.findExamBySectionAndStudent(
				sectionSeq,
				type,
				student.getSeq());
		return papers;
	}
	/**
	 * 어절 기반 시험 문제 결과
	 * @param sectionSeq
	 * @param licenseUuid
	 * @return
	 */
	public List<LearningPaper> findLearningPapersBySection(
			Integer sectionSeq,
			String licenseUuid) {
		License license = licenseService.findBy(License.Column.lcs_uuid, licenseUuid, true);
		User student = licenseToStudent(license);
		return learningPaperDao.findLearningPapersBySection(
				sectionSeq,
				student.getSeq());
	}
	
	private User licenseToStudent (License license) {
		User student = userService.findUser(license.getStudentRef());
		if (!student.isStudent()) {
			throw new AppException(ErrorCode.NOT_A_STUDENT, 422);
		}
		return student;
	}
	/**
	 * 주어진 학생의 단계별 학습 시험 이력
	 * @param studentSeq
	 * @param license
	 * @return
	 */
	public TypeMap findExamsByLicense(String studentId, String licenseUuid) {
		License license = licenseService.findBy(License.Column.lcs_uuid, licenseUuid);
		User student = licenseToStudent(license);
		if (!student.getUserId().equals(studentId)) {
			// student id mismatch
			throw new AppException(ErrorCode.VALUE_MISMATCH, 400, "student is not a license user");
		}
		List<ExamPaper> papers = examPaperDao.findExamsByStudent(student.getSeq());
		List<LearningPaper> learnings = learningPaperDao.findLearninsByStudent(student.getSeq());
		return TypeMap.with("papers", papers, "learnings", learnings);
	}
	/**
	 * 선생님 계정에서 주어진 라이선스의 학습 시험 이력 조회
	 * 
	 * 
	 * 
	 * @param teacherId
	 * @param license
	 * @return
	 */
	public TypeMap findExamsByStudentLicense(String teacherId, String licenseUuid) {
		User teacher = userService.findTeacher(teacherId);
		License license = licenseService.findBy(License.Column.lcs_uuid, licenseUuid);
		User student = licenseToStudent(license);
		// FIXME 자기 학생인지 확인해야 함 
		List<ExamPaper> papers = examPaperDao.findExamsByStudent(student.getSeq());
		List<LearningPaper> learnings = learningPaperDao.findLearninsByStudent(student.getSeq());
		return TypeMap.with("papers", papers, "learnings", learnings);
	}
	
	public TypeMap queryBySectionChunk(String licenseUuid) {
		License license = licenseService.findBy(License.Column.lcs_uuid, licenseUuid);
		User student = licenseToStudent(license);
		
		List<ExamPaper> papers = examPaperDao.findRecentExamsPerSegment(student.getSeq());
		return TypeMap.with("quiz", papers);
	}
	
	public List<ExamPaper> queryForExamPapers(PaperQuery query) {
		if(query.studentRef == null) {
			throw new AppException(ErrorCode.VALUE_MISMATCH, 400, "student required");
		}
		return this.examPaperDao.findExamsByQuery(query);
	}
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ExamPaper analyizeExamPaper(ExamPaper paper) {
		List<ExamAnswer> submissions = paper.getSubmissions();
		Section section = sectionService.findBy(Section.Column.seq, paper.getSectionRef());
		Map<Integer, Sentence> sentenceMap = section.getSentenceMap();
		ExamService.log.debug("[ANALYSIS] {} submissions for exam {}", submissions.size(), paper.getSeq());
		for (ExamAnswer sbm : submissions) {
			Sentence sen = sentenceMap.get(sbm.getSentenceRef());
			String analysis = null;
			if(sen != null) {
				String question = sen.getSentence();
				Map<String, int[]> mark = assessmentService.mark(question, sbm.getValue());
				analysis = Util.stringify(om, new TypeMap(mark));
			} else {
				/*
				 * 
				 */
				ExamService.log.warn("[MISSING] sentence: {} at section {} from exam {}. answer: {} ", 
						sbm.getSentenceRef(),
						section.getSeq(),
						paper.getSeq(),
						sbm.getValue());
				analysis = "{}";
			}
			sbm.setAnalysis(analysis);
			this.examAnswerDao.updateAnalysis(sbm);
		}
		paper.setAnalyzed(Boolean.TRUE);
		this.examPaperDao.updateAsAnalyzed(paper);
		return paper;
	}
	/**
	 * 분석해야할 시험 답안 조회
	 * @param limit
	 * @return
	 */
	public List<ExamPaper> findExamPapersToAnalyze(int limit) {
		return this.examPaperDao.findExamPapersToAnalyze(limit);
	}
}
