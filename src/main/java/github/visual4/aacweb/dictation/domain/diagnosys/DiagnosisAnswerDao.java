package github.visual4.aacweb.dictation.domain.diagnosys;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.Dao;

@Repository
public class DiagnosisAnswerDao {
	
	final SqlSession session;
	public DiagnosisAnswerDao(SqlSession session) {
		this.session = session;
	}

	public List<DiagnosisAnswer> findAnswersByStudent(Integer studetSeq) {
		return session.selectList(Dao.mapper(this, "findAnswersByStudent"), studetSeq);
	}

	public DiagnosisAnswer findAnswerByQuizAndStudent(DiagnosisAnswer query) {
		return Dao.selectOne(session, Dao.mapper(this, "findAnswerByQuizAndStudent"), query);	
	}

	public void insertAnswer(DiagnosisAnswer ans) {
		this.session.insert(Dao.mapper(this, "insertAnswer"), ans);
		
	}

	public void updateAnswer(DiagnosisAnswer ans) {
		Dao.updateOne(session, Dao.mapper(this, "updateAnswer"), ans);
	}

	public void updateCommit(DiagnosisAnswer ans) {
		Dao.updateOne(session, Dao.mapper(this, "updateCommit"), ans);
	}
}
