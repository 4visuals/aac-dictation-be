package github.visual4.aacweb.dictation.domain.diagnosys;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.visual4.aacweb.dictation.Res;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.chapter.Chapter;
import github.visual4.aacweb.dictation.domain.diagnosys.dto.StudentDiagnosisDto;
import github.visual4.aacweb.dictation.domain.exam.ExamPaper;
import github.visual4.aacweb.dictation.domain.exam.ExamService;
import github.visual4.aacweb.dictation.domain.exam.dto.PaperQuery;

@RestController
@RequestMapping("/api/diagnosis")
public class DiagnosisController {

	final DiagnosisService diagnosisService;
	final ExamService examService;
	
	public DiagnosisController(
			DiagnosisService diagnosisService,
			ExamService examService) {
		this.diagnosisService = diagnosisService;
		this.examService = examService;
	}
	
	@GetMapping("/student/{studentSeq}")
	public Object findDiagnsisByStudent(@PathVariable Integer studentSeq) {
		StudentDiagnosisDto diagnosis = diagnosisService.findDiagnosisByStudent(studentSeq);
		List<ExamPaper> exams = examService.queryForExamPapers(PaperQuery.byDiagnosis(studentSeq));
		return Res.success("diagnosis", diagnosis, "exams", exams);
	}
	@GetMapping("/chapter-form")
	public Object getAsChapterForm() {
		Chapter chapter = diagnosisService.getAsChapterForm();
		return Res.success("chapter", chapter);
	}
	@GetMapping("/difficulties")
	public Object parseDifficulties() {
		return this.diagnosisService.parseDifficulties();
	}

	@PostMapping("/{quizSeq}/student/{studentSeq}")
	public Object insertAnswer(
			@PathVariable Integer quizSeq,
			@PathVariable Integer studentSeq, 
			@RequestBody TypeMap answerDto) {
		DiagnosisAnswer ans = this.diagnosisService.putAnswer(studentSeq, quizSeq, answerDto.getStr("answer"));
		return ans;
	}
	@PutMapping("/student/{studentSeq}")
	public Object commitDiagnosis(
			@PathVariable Integer studentSeq, 
			@RequestBody TypeMap param) {
		String version = param.getStr("version");
		this.diagnosisService.commitDiagnosis(studentSeq, version);
		return param;
	}
	
}
