package github.visual4.aacweb.dictation.domain.order.group;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.Dao;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.order.group.GroupOrderForm.Column;

@Repository
public class GroupOrderDao {

	final SqlSession session;

	public GroupOrderDao(SqlSession session) {
		this.session = session;
	}
	
	public void createGroupOrderForm(GroupOrderForm form) {
		session.insert(Dao.mapper(this, "createGroupOrderForm"), form);
	}

	public List<GroupOrderForm> findOrders(Column column, Object value) {
		return session.selectList(Dao.mapper(this, "findOrders"),
				TypeMap.with("col", column, "value", value ));
	}
	/**
	 * 
	 * @param seq
	 * @param orderSeq
	 * @return
	 */
	public GroupOrderForm findBy(Column column, Object value) {
		return session.selectOne(
				Dao.mapper(this, "findBy"),
				TypeMap.with("col", column, "value", value ));
	}
	/**
	 * 단체 구매 문의 취소
	 * @param form - 취소시킬 단체 구매
	 * @return
	 */
	public int cancelOrder(GroupOrderForm form) {
		if (form.state == GroupOrderForm.OrderFormState.CMT 
				|| form.state == GroupOrderForm.OrderFormState.PND) {
			throw new AppException(ErrorCode.APP_BUG, 500, "invalidte group order state: " + form.state);
		}
		return session.update(Dao.mapper(this, "updateOrderState"), form);
	}
	/**
	 * 단체 구매 결제 완료 상태로 변경
	 * @param form
	 * @return
	 */
	public int commitOrder(GroupOrderForm form) {
		form.commit();
		return session.update(Dao.mapper(this, "updateOrderState"), form);
		
	}
}
