package github.visual4.aacweb.dictation.domain.exam.recent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import github.visual4.aacweb.dictation.domain.exam.ExamAnswer;
import github.visual4.aacweb.dictation.domain.exam.ExamPaper;
import github.visual4.aacweb.dictation.domain.exam.LearningPaper;
import github.visual4.aacweb.dictation.domain.sentence.Sentence.SentenceType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
/**
 * 학생이 가장 최근에 시도한 학습 이력을 추적하기 위해 사용함
 * 
 * (student, section, offset, type)마다 가장 최근의 학습 이력을 기록함
 * 
 * @author chminseo
 *
 * @param <T> - ExamPaper.class or LearningPaper.class
 */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RecentPaper {
	
	@EqualsAndHashCode.Include
	Long seq;
	
	Long studentRef;
	
	Integer sectionRef;
	
	Integer questionOffset;
	
	SentenceType type;
	/**
	 * 시험 문제 참조
	 */
	Long paperRef;
	/**
	 * 틀린 문항 갯수
	 */
	Integer numOfWrongAnswer;
	/**
	 * 전체 문항 갯수
	 */
	Integer numOfQuestions;
	/**
	 * 시도 횟수
	 */
	Integer trials;
	
	
	ExamPaper paper;

	public static RecentPaper from(ExamPaper paper) {
		RecentPaper  ans = new RecentPaper();
		ans.setStudentRef(paper.getStudentRef());
		ans.setSectionRef(paper.getSectionRef());
		ans.setQuestionOffset(paper.getQuestionOffset());
		ans.setType(paper.getType());
		
		ans.setPaperRef(paper.getSeq());
		ans.setNumOfQuestions(paper.getNumOfQuestions());
		long incorrects = paper.getSubmissions()
				.stream()
				.filter(submit -> !submit.getCorrect())
				.count();
		ans.setNumOfWrongAnswer((int)incorrects);
		ans.setTrials(1);
		return ans;
	}
	/**
	 * 새로운 시험 정보로 교체함
	 * @param paper
	 */
	public void replacePaper(ExamPaper paper) {
		
		this.setPaperRef(paper.getSeq());
		this.setNumOfQuestions(paper.getNumOfQuestions());
		long incorrects = paper.getSubmissions()
				.stream()
				.filter(submit -> !submit.getCorrect())
				.count();
		this.setNumOfWrongAnswer((int)incorrects);
		this.setTrials(this.trials+ 1);
		System.out.println("d");
		
	}
	
	public void filterSubmission(Predicate<ExamAnswer> predicate) {
		List<ExamAnswer> submissions = this.paper
				.getSubmissions()
				.stream()
				.filter(predicate)
				.collect(Collectors.toList());
		this.paper.setSubmissions(submissions);
	}
}
