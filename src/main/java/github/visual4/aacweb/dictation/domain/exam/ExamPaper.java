package github.visual4.aacweb.dictation.domain.exam;

import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;

import github.visual4.aacweb.dictation.domain.sentence.Sentence.SentenceType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * 시험에 대한 제출 답안
 * @author chminseo
 *
 */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ExamPaper {
	@EqualsAndHashCode.Include
	Long seq;
	/**
	 * fe에서 첨부하는 라이선스 uuid. 연결된 학생 계정을 찾을때 사용함
	 * @transient 
	 */
	String license;
	
	Integer sectionRef;
	Long studentRef; // Fk to User.seq
	Integer ageInMonth;
	
	SentenceType type;
	Instant startTime;
	Instant endTime;
	/**
	 * 문항 수
	 * db에는 풀지 않은 문제는 기록되지 않음(맞거나 틀린 문항만 기록함)
	 * 문항 수가 0이면 모든 문제를 풀었다는 뜻.
	 */
	Integer numOfQuestions;
	
	List<ExamAnswer> submissions;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("licesnse: ");
		sb.append(license);
		sb.append("sectionRef: ");
		sb.append(sectionRef);
		sb.append(", student: ");
		sb.append(studentRef);
		sb.append(", type: ");
		sb.append(type);
		sb.append('\n');
		for (int i = 0; i < submissions.size(); i++) {
			sb.append(submissions.get(i));
		}
		return sb.toString();
	}

	public void eachAnswer(Consumer<ExamAnswer> consumer) {
		for (ExamAnswer examAnswer : submissions) {
			consumer.accept(examAnswer);
		}
	}
	
}
