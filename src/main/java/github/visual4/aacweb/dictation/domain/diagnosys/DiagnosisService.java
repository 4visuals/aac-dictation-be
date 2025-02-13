package github.visual4.aacweb.dictation.domain.diagnosys;

import java.util.Arrays;
import java.util.Iterator;
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
import github.visual4.aacweb.dictation.domain.chapter.Chapter;
import github.visual4.aacweb.dictation.domain.diagnosys.dto.StudentDiagnosisDto;
import github.visual4.aacweb.dictation.domain.section.Section;
import github.visual4.aacweb.dictation.domain.sentence.Sentence;
import github.visual4.aacweb.dictation.korean.Mark;
import github.visual4.aacweb.dictation.service.analysis.AnalysisService;
import github.visual4.aacweb.dictation.tools.Origin;

@Service
@Transactional
public class DiagnosisService {

	private final static int DUMMY_SECTION_NUM = 100000;
	private final static int DUMMY_SETENCE_NUM = 200000;
	private final static Set<String> DIAGNOSIS_VERSION = Set.of("2024_v1", "2024_v2");
	final ObjectMapper om;
	final DiagnosisQuizDao quizDao;
	final DiagnosisAnswerDao answerDao;
	
	final AssessmentService assessmentService;
	final AnalysisService analysisService;
	final List<String> levels ;
	public DiagnosisService(
			ObjectMapper om,
			DiagnosisAnswerDao answerDao,
			DiagnosisQuizDao quizDao,
			AssessmentService assessmentService,
			AnalysisService analysisService) {
		this.om = om;
		this.quizDao = quizDao;
		this.answerDao = answerDao;
		this.assessmentService = assessmentService;
		this.analysisService = analysisService;
		this.levels = IntStream.rangeClosed(1, 50)
                .mapToObj(i -> String.format("L%02d", i))
                .collect(Collectors.toList());
		System.out.println("levels" + this.levels);
	}
	
	public StudentDiagnosisDto findDiagnosisByStudent(Integer studentSeq) {
		TypeMap diffMap = this.parseDifficulties();
		List<DiagnosisQuiz> quizes = quizDao.listQuiz();
		List<DiagnosisAnswer> answers = answerDao.findAnswersByStudent(studentSeq);
		for (DiagnosisQuiz quiz : quizes) {
			DiagnosisAnswer answer = answers.stream()
					.filter(ans -> ans.quizRef.equals(quiz.seq))
					.findFirst().orElse(null);
			quiz.setAnswer(answer);
			Map<String, Object> analysisMap = diffMap.get("" + quiz.getSeq());
			String json = Util.stringify(om, analysisMap);
			quiz.setAnalysis(json);
		}
		Map<String, List<DiagnosisQuiz>> group = quizes.stream()
	            .collect(Collectors.groupingBy(DiagnosisQuiz::getVersion));
		return new StudentDiagnosisDto(group.get("2024_v0"), group.get("2024_v1"), group.get("2024_v2"));
	}
	public DiagnosisAnswer putAnswer(Integer studentSeq, Integer quizSeq, String answer) {
		DiagnosisQuiz quiz = this.quizDao.findBySeq(quizSeq);
		DiagnosisAnswer query = DiagnosisAnswer.query(studentSeq, quizSeq); 
		DiagnosisAnswer ans = this.answerDao.findAnswerByQuizAndStudent(query);
		
		String analysis = this.buildAnalysis(quiz.getQuestion(), answer);
		
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
	 * 
	 * @param question 제시된 문제
	 * @param answer 문제에 대한 제출 답안
	 * @return level별 맞춘문제와 전체 문제의 데이터를 json형태로 반환
	 */
	private String buildAnalysis(String question, String answer) {
		Map<String, int[]> mark = assessmentService.mark(question, answer);
		return Util.stringify(om, new TypeMap(mark));
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
	/**
	 * 진단평가 용 문제도 형태소 분석이 필요하다.
	 * @return 
	 */
	public TypeMap parseDifficulties() {
		List<DiagnosisQuiz> questions = this.quizDao.listQuiz();
		
		List<Sentence> sentences = questions.stream().map(quiz -> {
			Sentence sen = new Sentence();
			sen.setSentence(quiz.getQuestion());
			sen.setSeq(quiz.getSeq());
			return sen;
		}).collect(Collectors.toList());
		Section section = new Section();
		section.setSeq(DUMMY_SECTION_NUM);
		section.setSentences(sentences);
		
		return analysisService.parseDifficulties(section);
	}
	public TypeMap parseDifficulty(String sentence) {
		TypeMap dfMap = new TypeMap();
		Mark mark = analysisService.parseDifficulties(sentence);
		dfMap.put("" +0, mark.toMap());
		return dfMap;
	}

	public Chapter getAsChapterForm() {
		List<DiagnosisQuiz> questions = this.quizDao.listQuiz();
		
		List<Sentence> sentences = questions.stream().map(quiz -> {
			Sentence sen = new Sentence();
			sen.setSentence(quiz.getQuestion());
			sen.setSeq(quiz.getSeq());
			return sen;
		}).collect(Collectors.toList());
		Section section = new Section();
		section.setLevel(100); // 그냥 정함 0보다 크면 됨
		section.setSeq(DUMMY_SECTION_NUM);
		section.setSentences(sentences);
		section.setDescription("진단평가");
		Chapter chapter = new Chapter(50, "진단평가", Origin.C);
		chapter.setSections(Arrays.asList(section));
		return chapter;
	}
}
