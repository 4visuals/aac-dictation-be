package github.visual4.aacweb.dictation.domain.product;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.Dao;
import github.visual4.aacweb.dictation.TypeMap;

@Repository
public class ProductDao {

	final SqlSession session;

	public ProductDao(SqlSession session) {
		super();
		this.session = session;
	}

	public Product findBy(Product.Column col, Object value) {
		return session.selectOne(Dao.mapper(this, "findBy"), TypeMap.with("colname", col.name(), "value", value));
	}
	
	
	
}
