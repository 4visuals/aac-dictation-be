package github.visual4.aacweb.dictation.domain.order.delivery;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.Dao;

@Repository
public class DeliveryInfoDao {

	final SqlSession session;
	public DeliveryInfoDao(SqlSession session) {
		this.session = session;
	}
	
	public void insertDeliveryInfo(DeliveryInfo info) {
		this.session.insert(Dao.mapper(this, "insertDeliveryInfo"), info);
	}
}
