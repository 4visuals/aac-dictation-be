package github.visual4.aacweb.dictation.domain.diagnosys.dto;

import java.util.List;

import github.visual4.aacweb.dictation.domain.diagnosys.DiagnosisQuiz;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentDiagnosisDto {
	
	private List<DiagnosisQuiz> v1;
	private List<DiagnosisQuiz> v2;

	private boolean v1Ready;
	private boolean v2Ready;
	public StudentDiagnosisDto(List<DiagnosisQuiz> v1, List<DiagnosisQuiz> v2 ) {
		this.v1 = v1;
		this.v2 = v2;
		this.v1Ready = allFiied(v1);
		this.v2Ready = allFiied(v2);
	}
	private static boolean allFiied(List<DiagnosisQuiz> quizes) {
		for (DiagnosisQuiz quiz : quizes) {
			if(!quiz.checkIfCommitted()) {
				return false;
			}
		}
		return true;
	}
}
