package github.visual4.aacweb.dictation.domain.order.group;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.Dao;
import github.visual4.aacweb.dictation.TypeMap;

@Repository
public class GroupPaperDao {

	final SqlSession session;
	
	public GroupPaperDao(SqlSession session) {
		this.session = session;
	}
	/**
	 * 단체 주문에 포함된 증빙서류 목록 추가
	 * @param groupOrderFormSeq
	 * @param paper
	 */
	public void insertOrderPaper(Integer groupOrderFormSeq, OrderPaper paper) {
		session.insert(Dao.mapper(this, "insertOrderPaper"), 
				TypeMap.with("formSeq", groupOrderFormSeq, "paper", paper));
		
	}

}
