package github.visual4.aacweb.dictation.domain.exam.recent;

import java.util.List;

import org.springframework.stereotype.Service;

import github.visual4.aacweb.dictation.domain.exam.ExamPaper;

@Service
public class RecentPaperService {

	final RecentPaperDao recentPaperDao;
	
	public RecentPaperService(RecentPaperDao recentPaperDao) {
		this.recentPaperDao = recentPaperDao;
	}
	
	public void insertRecentPaper(ExamPaper paper) {
		RecentPaper query = RecentPaper.from(paper);
		RecentPaper existingPaper = recentPaperDao.findExistingPaper(query);
		if (existingPaper != null) {
			// 이전에 풀었던 기록이 존재함
			existingPaper.replacePaper(paper);
			recentPaperDao.updateForReplacement(existingPaper);
		}else {
			recentPaperDao.insertRecentPaper(query);
		}
	}
	public List<RecentPaper> findWrongAnswersByStudent(Long teacherSeq, Long studentSeq) {
		return recentPaperDao.findRecentPapersByStudent(studentSeq);
	}
	/**
	 * 틀린 문제들만 반환
	 * @param studentSeq
	 * @param sectionSeq
	 * @return
	 */
	public List<RecentPaper> findWrongAnswers(Long studentSeq, Integer sectionSeq) {
		List<RecentPaper> papers = recentPaperDao.findRecentPapers(studentSeq, sectionSeq);
		for (RecentPaper recentPaper : papers) {
			recentPaper.filterSubmission(submit -> !submit.getCorrect());
		}
		return papers;
	}
}
