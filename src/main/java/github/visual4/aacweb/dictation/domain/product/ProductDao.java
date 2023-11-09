package github.visual4.aacweb.dictation.domain.product;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.AppStatus;
import github.visual4.aacweb.dictation.Dao;
import github.visual4.aacweb.dictation.ErrorCode;
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

	public List<Product> findAllProducts() {
		return session.selectList(Dao.mapper(this, "findAllProducts"));
	}

	public List<Product> findProducts(AppStatus appStatus) {
		return session.selectList(Dao.mapper(this, "findProducts"), appStatus.name());
	}

	public void insertProduct(Product product) {
		session.insert(Dao.mapper(this, "insertProduct"), product);
	}
	/**
	 * 상품 기본 정보 수정(상품명, 상세 내용)
	 * @param product
	 */
	public void updateBasicInfo(Product product) {
		int cnt = session.update(Dao.mapper(this, "updateBasicInfo"), product);
		if(cnt > 1) {
			throw new AppException(ErrorCode.APP_BUG, 500, "multiple rows updated by updateBasicInfo(product: " + product.seq + ")");
		}
	}
	public void updateAsExpired(Product product) {
		Dao.updateOne(session, Dao.mapper(this, "updateAsExpired"), product);
	}
}
