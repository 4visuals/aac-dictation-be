package github.visual4.aacweb.dictation.domain.exam;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

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

}
