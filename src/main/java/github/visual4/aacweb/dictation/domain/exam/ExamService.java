package github.visual4.aacweb.dictation.domain.exam;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.domain.license.License;
import github.visual4.aacweb.dictation.domain.license.LicenseService;
import github.visual4.aacweb.dictation.domain.section.Section;
import github.visual4.aacweb.dictation.domain.section.SectionService;
import github.visual4.aacweb.dictation.domain.user.User;
import github.visual4.aacweb.dictation.domain.user.UserService;

@Service
@Transactional
public class ExamService {
	final LicenseService licenseService;
	final UserService userService;
	final ExamPaperDao examPaperDao;
	final ExamAnswerDao examAnswerDao;
	final EojeolAnswerDao eojeolAnswerDao;
	final SectionService sectionService;

	public ExamService(
			LicenseService licenseService,
			UserService userService,
			ExamPaperDao examPaperDao,
			ExamAnswerDao examAnswerDao,
			EojeolAnswerDao eojeolAnswerDao,
			SectionService sectionService) {
		super();
		this.licenseService = licenseService;
		this.userService = userService;
		this.examPaperDao = examPaperDao;
		this.examAnswerDao = examAnswerDao;
		this.eojeolAnswerDao = eojeolAnswerDao;
		this.sectionService = sectionService;
	}

	/**
	 * 시험 답안 제출.
	 * 시험대상 - 낱말퀴즈, 문장퀴즈, 종합테스트에 해당함
	 * 
	 * @param examPaper
	 */
	public void insertExamPaper(ExamPaper examPaper) {
		String licenseUuid = examPaper.getLicense();
		License license = licenseService.findBy(License.Column.lcs_uuid, licenseUuid);
		//FIXME  not null license
		if (!license.isAlive(Instant.now())) {
			throw new AppException(ErrorCode.LICENSE_EXPIRED, 410, licenseUuid);
		}
		
		User student = userService.findUser(license.getStudentRef());
		// FIXME  not null student
		if (!student.isStudent()) {
			throw new AppException(ErrorCode.NOT_A_STUDENT, 422);
		}
		User teacher = userService.findTeacher(student.getTeacherRef());
		if (!teacher.isLicenseOwner(license)) {
			throw new AppException(ErrorCode.NOT_A_LICENSE_OWNER, 422);
		}
		Section section = sectionService.findBy(Section.Column.seq, examPaper.getSectionRef());
		long numOfQuestions = section
				.getSentences()
				.stream()
				.filter(sen -> sen.getType() == examPaper.getType()).count();
		examPaper.setStudentRef(student.getSeq());
		examPaper.setAgeInMonth(student.getAgeInMonth(Instant.now()));
		examPaper.setNumOfQuestions((int)numOfQuestions);
	
		examPaperDao.insertExamPaper(examPaper);
		
		examPaper.eachAnswer(answer -> {
			answer.setExamRef(examPaper.getSeq());
			examAnswerDao.insertExamAnswer(answer);
		});
	}
	/**
	 * 어절에 대한 학생들의 입력 데이터(학습 모드에서 입력된 값들)
	 * 
	 * @param eojelPaper
	 */
	public void insertEojeolPaper(EojeolPaper eojeolPaper) {
		String uuid = eojeolPaper.getLicense();
		License license = licenseService.findBy(License.Column.lcs_uuid, uuid, true);
		User student = userService.findUser(license.getStudentRef());
		if (!student.isStudent()) {
			throw new AppException(ErrorCode.NOT_A_STUDENT, 422);
		}
		User teacher = userService.findTeacher(student.getTeacherRef());
		if (!teacher.isLicenseOwner(license)) {
			throw new AppException(ErrorCode.NOT_A_LICENSE_OWNER, 422);
		}
		eojeolPaper.forEachAnswer(answer -> {
			// FIXME 학생 생일을 조회해서 처리해야함 
			answer.setAgeInMonth(36);
			answer.setStudentRef(student.getSeq());
		});
		eojeolAnswerDao.insertAnswer(eojeolPaper.getSubmissions());
	}
}
