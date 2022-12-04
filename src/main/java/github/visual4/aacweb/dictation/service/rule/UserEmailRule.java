package github.visual4.aacweb.dictation.service.rule;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;

public class UserEmailRule implements IRule {

	private void checkLength(String userId, int min, int max) {
		
		if (userId.length() < min || userId.length() > max) {
			throw new AppException(ErrorCode.USER_EMAIL_FORMAT, 422);
		}
	}
	@Override
	public String checkRule(String email) {
		int totalLen = 127;
		email = email.trim();
		String [] parts = email.split("@");
		if (parts.length != 2) {
			throw new AppException(ErrorCode.USER_EMAIL_FORMAT, 422);
		}
		String user = parts[0].trim();
		String domain = parts[1].trim().toLowerCase();
		checkLength(user, 1, totalLen);
		checkLength(domain.trim(), 4, totalLen - user.length());
		
		if (!domain.matches("(?:[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?\\.)+[a-z0-9][a-z0-9-]{0,61}[a-z0-9]")) {
			throw new AppException(ErrorCode.USER_EMAIL_FORMAT, 422);
		}
		if (!domain.contains(".")) {
			throw new AppException(ErrorCode.USER_EMAIL_FORMAT, 422);
		}
		return user + "@" + domain;
	}

}
