package github.visual4.aacweb.dictation.domain.diagnosys;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import github.visual4.aacweb.dictation.BaseDao;

@Import(DiagnosisAnswerDao.class)
class DiagnosisAnswerDaoTest extends BaseDao {

	@Autowired
	DiagnosisAnswerDao dao;
	
	@Test
	void test() {
		assertNotNull(dao);
		List<DiagnosisAnswer> answers = dao.findAnswersByStudent(43);
		assertNotNull(answers);
		assertEquals(1, answers.size());
		
		answers = dao.findAnswersByStudent(22);
		assertEquals(0, answers.size());
		
		assertNotNull(dao.findAnswerByQuizAndStudent(DiagnosisAnswer.query(43, 1)));
	}

}
