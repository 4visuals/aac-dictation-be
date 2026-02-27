package github.visual4.aacweb.dictation.domain.quotationform;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.Dao;

@Repository
public class QuotationFormItemDao {

	final SqlSession session;

	public QuotationFormItemDao(SqlSession session) {
		this.session = session;
	}

	public void insertItem(QuotationFormItem item) {
		session.insert(Dao.mapper(this, "insertItem"), item);
	}

	public void deleteItemsByFormRef(Long formRef) {
		session.delete(Dao.mapper(this, "deleteItemsByFormRef"), formRef);
	}
}
