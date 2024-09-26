package github.visual4.aacweb.dictation.domain.diagnosys;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.Util;
import github.visual4.aacweb.dictation.domain.assessment.AssessmentService;
import github.visual4.aacweb.dictation.domain.diagnosys.dto.StudentDiagnosisDto;

@Service
@Transactional
public class DiagnosisService {

	private final static Set<String> DIAGNOSIS_VERSION = Set.of("2024_v1", "2024_v2");
	final ObjectMapper om;
	final DiagnosisQuizDao quizDao;
	final DiagnosisAnswerDao answerDao;
	
	final AssessmentService assessmentService;
	final List<String> levels ;
	public DiagnosisService(
			ObjectMapper om,
			DiagnosisAnswerDao answerDao,
			DiagnosisQuizDao quizDao,
			AssessmentService assessmentService) {
		this.om = om;
		this.quizDao = quizDao;
		this.answerDao = answerDao;
		this.assessmentService = assessmentService;
		this.levels = IntStream.rangeClosed(1, 50)
                .mapToObj(i -> String.format("L%02d", i))
                .collect(Collectors.toList());
		System.out.println("levels" + this.levels);
	}
	
	public StudentDiagnosisDto findDiagnosisByStudent(Integer studentSeq) {
		List<DiagnosisQuiz> quizes = quizDao.listQuiz();
		List<DiagnosisAnswer> answers = answerDao.findAnswersByStudent(studentSeq);
		for (DiagnosisQuiz quiz : quizes) {
			DiagnosisAnswer answer = answers.stream()
					.filter(ans -> ans.quizRef.equals(quiz.seq))
					.findFirst().orElse(null);
			quiz.setAnswer(answer);
		}
		Map<String, List<DiagnosisQuiz>> group = quizes.stream()
	            .collect(Collectors.groupingBy(DiagnosisQuiz::getVersion));
		return new StudentDiagnosisDto(group.get("2024_v1"), group.get("2024_v2"));
	}
	public DiagnosisAnswer putAnswer(Integer studentSeq, Integer quizSeq, String answer) {
		DiagnosisQuiz quiz = this.quizDao.findBySeq(quizSeq);
		DiagnosisAnswer query = DiagnosisAnswer.query(studentSeq, quizSeq); 
		DiagnosisAnswer ans = this.answerDao.findAnswerByQuizAndStudent(query);
		
		Map<String, int[]> mark = assessmentService.mark(quiz.getQuestion(), answer);
		String analysis = Util.stringify(om, new TypeMap(mark));
		
		if(ans == null) {
			ans = DiagnosisAnswer.init(studentSeq, quizSeq, answer, analysis);
			answerDao.insertAnswer(ans);
		} else {
			ans.setAnswer(answer);
			ans.setAnalysis(analysis);
			answerDao.updateAnswer(ans);
		}
		return ans;
	}
	/**
	 * 주어진 버전의 진단평가 입력 완료함
	 * @param studentSeq
	 * @param version
	 */
	public void commitDiagnosis(Integer studentSeq, String version) {
		checkVersion(version);
		List<DiagnosisAnswer> all = answerDao.findAnswersByStudent(studentSeq);
		List<DiagnosisAnswer> answers = all
				.stream()
				.filter(ans -> ans.matchedVersion(version))
				.collect(Collectors.toList());
		for (DiagnosisAnswer answer : answers) {
			answer.setCommit(Boolean.TRUE);
			answerDao.updateCommit(answer);
		}
	}
	private void checkVersion (String version) {
		if(!DIAGNOSIS_VERSION.contains(version)) {
			throw new AppException(ErrorCode.INVALID_VALUE2, 400, "check version [" + version + "]");
		}
	}
}
