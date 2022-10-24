package github.visual4.aacweb.dictation.domain.policy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.visual4.aacweb.dictation.domain.policy.Policy.PolicyType;

@Service
@Transactional
public class PolicyService {

	final PolicyDao policyDao;
	public PolicyService(PolicyDao policyDao) {
		this.policyDao = policyDao;
	}
	
	public Policy findLatestPolicyBy(PolicyType type) {
		return policyDao.findLatestPolicyBy(type);
	}
	public Map<PolicyType, Policy> findLatestPolicies() {
		List<Policy> policies = policyDao.findLatestPolicies();
		Map<PolicyType, Policy> map = new HashMap<>();
		for (Policy policy : policies) {
			map.put(policy.getPolicyType(), policy);
		}
		return map;
	}
}
