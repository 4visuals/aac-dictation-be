package github.visual4.aacweb.dictation.domain.diagnosys;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import github.visual4.aacweb.dictation.BaseDao;
import github.visual4.aacweb.dictation.domain.chapter.ChapterDao;

@Import(DiagnosisQuizDao.class)
class DiagnosisQuizDaoTest extends BaseDao {

	@Autowired
	DiagnosisQuizDao dao;
	@Test
	void test() {
		assertNotNull(dao);
		List<DiagnosisQuiz> quiz = dao.listQuiz();
		assertEquals(20, quiz.size());
		
		assertNotNull(dao.findBySeq(2));
	}

}
