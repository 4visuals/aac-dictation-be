package github.visual4.aacweb.dictation.domain.exam;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

import github.visual4.aacweb.dictation.domain.sentence.Sentence.SentenceType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
/**
 * FIXME  EojeolPaper로 이름 변경해야함
 * 
 * @author chminseo
 *
 */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class LearningPaper {

	@EqualsAndHashCode.Include
	Long seq;
	
	String license;
	
	Integer sectionRef;
	
	Long studentRef; // Fk to User.seq
	Integer ageInMonth;
	
	Instant startTime;
	Instant endTime;
	Integer questionOffset;
	/**
	 * 문항 수
	 */
	Integer numOfQuestions;
	
	List<EojeolAnswer> submissions;
	
	
	/**
	 * W - [낱말학습]에서 입력됨
	 * S - [문장학습]에서 입력됨
	 */
	SentenceType type;
	/**
	 * 학습 종류(보고쓰기,학습,퀴즈)
	 */
	ExamMode mode;
	public void forEachAnswer(Consumer<EojeolAnswer> fn) {
		submissions.forEach(fn);
	}
	
	public String getYMD() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
	            .withZone(ZoneId.systemDefault());
		return formatter.format(this.startTime);
	}
}
