package github.visual4.aacweb.dictation.domain.order;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.Dao;
import github.visual4.aacweb.dictation.TypeMap;

@Repository
public class OrderDao {

	final SqlSession session;
	
	public OrderDao(SqlSession session) {
		this.session = session;
	}
	
	public Order findBy(Order.Column column, Object value) {
		return session.selectOne(Dao.mapper(this, "findBy"),
				TypeMap.with("colname", column.name(), "value", value));
	}
	public Order insertOrder(Order order) {
		session.insert(Dao.mapper(this, "insertOrder"), order);
		return order;
	}

	public List<Order> findOrders() {
		return session.selectList(Dao.mapper(this, "findOrders"));
	}
	/**
	 * 결제를 승인함
	 * @param order
	 */
	public void activateOrder(Order order) {
		session.update(Dao.mapper(this, "activateOrder"), order);
		
	}

}
