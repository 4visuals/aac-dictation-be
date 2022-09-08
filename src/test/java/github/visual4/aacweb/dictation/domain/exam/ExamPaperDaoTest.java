package github.visual4.aacweb.dictation.domain.exam;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import github.visual4.aacweb.dictation.BaseDao;
import github.visual4.aacweb.dictation.domain.sentence.Sentence.SentenceType;

@Import(ExamPaperDao.class)
class ExamPaperDaoTest extends BaseDao {

	@Autowired
	ExamPaperDao examDao;
	
	@Test
	void test_섹션별_학생의_시험결과() {
		 List<ExamPaper> papers = examDao.findExamBySectionAndStudent(1, SentenceType.W, 45L);
//		 System.out.println(paper);
		 for (ExamPaper paper : papers) {
			System.out.println("seq: " + paper.getSeq() + ", " + paper.getYMD());
			for (ExamAnswer answer : paper.getSubmissions()) {
				System.out.println(" > " + answer.getValue() + ", " + answer.getCorrect());
			}
		}
	}
	
	@DisplayName("학생의 단계별학습 시험 정보")
	@Test
	void test_query_student_exams() {
		List<ExamPaper> papers = examDao.findExamsByStudent(43L);
		for (ExamPaper paper : papers) {
			System.out.println(paper);
		}
	}
	@DisplayName("학생의 세그먼트별 최근 시험 정보")
	@Test
	public void last_exams_per_section_slice() {
		List<ExamPaper> exams = examDao.findRecentExamsPerSegment(43L);
		for (ExamPaper exam : exams) {
			System.out.println(exam);
		}
	}
	
	@Test
	void test_timezone() {
		List<ExamPaper> exams = examDao.findExamsByStudent(43L);
		ExamPaper exam = exams.stream().filter(ex -> ex.getSeq().intValue() == 838).findFirst().get();
		System.out.println(exam.getSeq() + ", " + exam.getStartTime());
		System.out.println(exam.getYMD());
	}

}
