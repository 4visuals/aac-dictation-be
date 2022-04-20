package github.visual4.aacweb.dictation.domain.exam;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class ExamAnswer {
	@EqualsAndHashCode.Include
	Long examRef;        // FK to ExamPaper.seq
	@EqualsAndHashCode.Include
	Integer sentenceRef; // FK to Sentence.seq
	
	Boolean correct;
	String value;        // 제출한 답안
	long elapsedTimeMillis; //입력 시간(millis)
	
	/**
	 * @transient;
	 */
	String mode;
}
