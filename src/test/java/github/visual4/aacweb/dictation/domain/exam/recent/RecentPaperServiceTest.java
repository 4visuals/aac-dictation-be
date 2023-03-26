package github.visual4.aacweb.dictation.domain.exam.recent;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import github.visual4.aacweb.dictation.BaseDao;
import github.visual4.aacweb.dictation.domain.exam.ExamPaper;
import github.visual4.aacweb.dictation.domain.exam.ExamPaperDao;

@Import({RecentPaperDao.class, RecentPaperService.class, ExamPaperDao.class})
class RecentPaperServiceTest extends BaseDao{

	@Autowired
	RecentPaperService recentPaperService;
	
	@Autowired
	ExamPaperDao examPaperDao;
	
	@Autowired
	RecentPaperDao recentPaperDao;
	
	@Test
	void test_replacement() {
		assertNotNull(recentPaperService);
		ExamPaper paper = testExamPaper();
		
		RecentPaper query = RecentPaper.from(paper);
		
		RecentPaper existing = recentPaperDao.findExistingPaper(query);
		Integer prevTrial = existing.getTrials();
		
		recentPaperService.insertRecentPaper(paper);
		
		RecentPaper updated = recentPaperDao.findExistingPaper(query);
		
		assertEquals(updated.getTrials(), prevTrial + 1);
		
	}

	private ExamPaper testExamPaper() {
		return examPaperDao.findExamsByStudent(429L).get(0);
	}

}
