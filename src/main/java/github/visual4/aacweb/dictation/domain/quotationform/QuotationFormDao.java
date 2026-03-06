package github.visual4.aacweb.dictation.domain.quotationform;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.Dao;

@Repository
public class QuotationFormDao {

	final SqlSession session;

	public QuotationFormDao(SqlSession session) {
		this.session = session;
	}

	public void insertForm(QuotationForm form) {
		session.insert(Dao.mapper(this, "insertForm"), form);
	}

	public List<QuotationForm> findQuotationForms() {
		return session.selectList(Dao.mapper(this, "findQuotationForms"));
	}

	public QuotationForm findQuotationFormBySeq(Long seq) {
		return session.selectOne(Dao.mapper(this, "findQuotationFormBySeq"), 
				github.visual4.aacweb.dictation.TypeMap.with("seq", seq));
	}

	public void updateForm(QuotationForm form) {
		Dao.updateOne(session, Dao.mapper(this, "updateForm"), form);
	}

	public void closeForm(QuotationForm form) {
		Dao.updateOne(session, Dao.mapper(this, "closeForm"), form);
	}
}
