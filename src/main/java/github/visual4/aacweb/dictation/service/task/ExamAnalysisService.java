package github.visual4.aacweb.dictation.service.task;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import github.visual4.aacweb.dictation.domain.exam.ExamPaper;
import github.visual4.aacweb.dictation.domain.exam.ExamService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExamAnalysisService {

	final ExamService examService;
	
	public ExamAnalysisService(ExamService examService) {
		this.examService = examService;
	}
	/**
	 * 학생이 제출한 받아쓰기(ExamPaper)에 대한 상세 분석을 실행함 
	 */
	@Scheduled(fixedRate = 3000)
	public void analyzieExamPapers() {
		List<ExamPaper> papers = examService.findExamPapersToAnalyze(10);
		ExamAnalysisService.log.debug("[ANALYSIS] {} exams", papers.size());
		for (ExamPaper paper : papers) {
			examService.analyizeExamPaper(paper);
		}
	}
}
