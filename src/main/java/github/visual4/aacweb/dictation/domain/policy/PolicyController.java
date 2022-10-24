package github.visual4.aacweb.dictation.domain.policy;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.visual4.aacweb.dictation.Res;
import github.visual4.aacweb.dictation.domain.policy.Policy.PolicyType;

@RestController
@RequestMapping("/api")
public class PolicyController {

	@Autowired
	PolicyService policyService;
	
	@GetMapping("/policy/{type}")
	public Object findLatestPolicyBy(@PathVariable PolicyType type) {
		Policy policy = policyService.findLatestPolicyBy(type);
		return Res.success("policy", policy);
	}
	@GetMapping("/policies")
	public Object findLatestPolicies() {
		Map<PolicyType, Policy> policies = policyService.findLatestPolicies();
		return Res.<PolicyType, Policy>success(policies);
	}
}
