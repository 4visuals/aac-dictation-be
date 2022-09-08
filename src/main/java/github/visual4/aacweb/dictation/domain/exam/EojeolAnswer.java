package github.visual4.aacweb.dictation.domain.exam;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 연습모드에서 각 어절에 대한 학생의 입력
 * 
 * 1) 시험모드(낱말퀴즈, 문장퀴즈)에서는 학생의 입력을 문장 단위로 처리함(ExamAnswer)
 * 2) 연습모드(낱말학습, 문장학습)에서는 학생의 입력을 어절 단위로 처리함(EojeolAnswer)
 * 
 * @author chminseo
 *
 */
@Getter
@Setter
@ToString
public class EojeolAnswer {
	@EqualsAndHashCode.Include
	Long examRef;
	@EqualsAndHashCode.Include
	Integer sentenceRef;
	@EqualsAndHashCode.Include
	Integer eojeolRef;
	
	Long studentRef;
	Integer ageInMonth;
	String value;
	Boolean correct;
	long elapsedTimeMillis; //입력 시간(millis)
	
	/**
	 * @transient
	 */
	String mode;
}
