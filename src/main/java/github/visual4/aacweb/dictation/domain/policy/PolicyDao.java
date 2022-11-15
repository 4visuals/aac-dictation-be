package github.visual4.aacweb.dictation.domain.policy;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.Dao;
import github.visual4.aacweb.dictation.domain.policy.Policy.PolicyType;

@Repository
public class PolicyDao {

	final private SqlSession session;
	
	public PolicyDao(SqlSession session) {
		this.session = session;
	}
	
	public Policy findLatestPolicyBy(PolicyType type) {
		return session.selectOne(Dao.mapper(this, "findLatestPolicyBy"), type.name());
	}
	public List<Policy> findLatestPolicies() {
		return session.selectList(Dao.mapper(this, "findLatestPolicies"));
	}

	public List<Policy> findPolicyHistoriesBy(PolicyType type) {
		return session.selectList(Dao.mapper(this, "findPolicyHistoriesBy"), type.name());
	}
	/**
	 * 새로운 정책 추가
	 * @param policy
	 * @return
	 */
	public Policy insertPolicy(Policy policy) {
		session.insert(Dao.mapper(this, "insertPolicy"), policy);
		return policy;
	}

}
