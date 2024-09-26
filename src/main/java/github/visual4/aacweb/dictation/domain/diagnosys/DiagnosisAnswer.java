package github.visual4.aacweb.dictation.domain.diagnosys;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DiagnosisAnswer {

	@EqualsAndHashCode.Include
	Integer seq;
	
	String answer;
	Integer studentRef;
	Integer quizRef;
	String analysis;
	Boolean commit;
	/**
	 * @transient 연결된 DaignosisQuestion의 version을 가져옴
	 */
	String version;
	
	public static DiagnosisAnswer query(Integer studentSeq, Integer quizSeq) {
		DiagnosisAnswer query = new DiagnosisAnswer();
		query.quizRef = quizSeq;
		query.studentRef = studentSeq;
		return query;
	}
	public static DiagnosisAnswer init(Integer studentSeq, Integer quizSeq, String answer, String analysis) {
	    DiagnosisAnswer newAnswer = new DiagnosisAnswer();
	    newAnswer.setStudentRef(studentSeq);
	    newAnswer.setQuizRef(quizSeq);
	    newAnswer.setAnswer(answer);
	    newAnswer.setAnalysis(analysis);
	    newAnswer.setCommit(Boolean.FALSE);
	    return newAnswer;
	}
	public boolean matchedVersion(String version) {
		return this.version.equals(version);
	}
}
