package github.visual4.aacweb.dictation.domain.exam.recent;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;

import github.visual4.aacweb.dictation.BaseDao;
import github.visual4.aacweb.dictation.domain.exam.ExamPaper;
import github.visual4.aacweb.dictation.domain.exam.ExamPaperDao;
import github.visual4.aacweb.dictation.domain.exam.recent.RecentPaper;
import github.visual4.aacweb.dictation.domain.exam.recent.RecentPaperDao;


@Import({RecentPaperDao.class, ExamPaperDao.class})
class RecentPaperDaoTest extends BaseDao{

	@Autowired
	RecentPaperDao recentPaperDao;
	
	@Autowired
	ExamPaperDao sentenceExamDao;
	
	@Test
//	@Rollback(false)
	void test() {
		
		List<ExamPaper> papers = sentenceExamDao.findExamsByStudent(429L);
		assertEquals(1, papers.size());
		
		RecentPaper recentPaper = RecentPaper.from(papers.get(0));
		recentPaperDao.insertRecentPaper(recentPaper);
	}
	
	@Test
	void test_load_exam_paper() {
		List<RecentPaper> recent = recentPaperDao.findRecentPapers(429L, 1);
		assertNotNull(recent);
	}

}
