package github.visual4.aacweb.dictation.domain.diagnosys;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DiagnosisQuiz {

	/**
	 * PK to dt_diagnosis_quiz
	 */
	Integer seq;
	String question;
	String version;
	
	DiagnosisAnswer answer;
	/**
	 * 문제 자체에 대한 레빌 분석 정보(json)
	 */
	String analysis;

	public Boolean checkIfCommitted() {
		if(this.answer == null) {
			return Boolean.FALSE;
		}
		return this.answer.getCommit();
	}
}
