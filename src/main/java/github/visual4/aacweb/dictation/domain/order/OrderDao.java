package github.visual4.aacweb.dictation.domain.order;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.Dao;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.order.Order.Column;

@Repository
public class OrderDao {

	final SqlSession session;
	
	public OrderDao(SqlSession session) {
		this.session = session;
	}
	
	public Order findOneBy(Order.Column column, Object value) {
		List<Order> orders = findBy(column, value);
		if (orders.size() > 1) {
			throw new AppException(ErrorCode.APP_BUG, 500, "multiple rows found: " + orders.size());
		}
		return orders.size() == 0 ? null : orders.get(0);
	}
	/**
	 * 주어진 사용자의 구매 내역
	 * @param customerRef
	 * @param teacherSeq
	 * @return
	 */
	public List<Order> findBy(Column column, Object value) {
		return session.selectList(Dao.mapper(this, "findBy"),
				TypeMap.with("colname", column.name(), "value", value));
	}
	/**
	 * 실제 구매 주문 내역 - 가입 시 제공되는 무료 주문 내역 등은 제외함.
	 * 
	 * @param teacherSeq
	 * @return
	 */
	public List<Order> findPurchasedOrders(Long teacherSeq) {
		return session.selectList(Dao.mapper(this, "findPurchasedOrders"), teacherSeq);
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
	/**
	 * 주문 상태 갱신
	 * @param order
	 */
	public void updateState(Order order) {
		session.update(Dao.mapper(this, "updateState"), order);
	}
}
