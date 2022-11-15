package github.visual4.aacweb.dictation.service.rule;

import java.util.HashMap;
import java.util.Map;

public class Rules {

	private final static String RULE_PASSWORD = "PASSWORD";
	private static final String RULE_USER_ID = "USER_ID";
	private static final String RULE_USER_NAME = "USER_NAME";
	
	private final static Map<String, IRule> rules = new HashMap<>();
	
	static {
		rules.put(RULE_PASSWORD, new PasswordRule());
		rules.put(RULE_USER_ID, new UserIdRule());
		rules.put(RULE_USER_NAME, new UserNameRule());
	}
	
	public static String checkPassword(String password) {
		IRule rule = rules.get(RULE_PASSWORD);
		return rule.checkRule(password);
	}
	public static String checkUserId(String userId) {
		IRule rule = rules.get(RULE_USER_ID);
		return rule.checkRule(userId);
	}
	public static String checkUserName(String name) {
		IRule rule = rules.get(RULE_USER_NAME);
		return rule.checkRule(name);
	}
}
