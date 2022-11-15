package github.visual4.aacweb.dictation.service.rule;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;

public class UserIdRule implements IRule {

	@Override
	public String checkRule(String userId) {
		userId = userId.trim();
		if (userId.length() < 4 || userId.length() > 20) {
			throw new AppException(ErrorCode.USER_ID_LENGTH, 422);
		}
		if (!userId.matches(".*[a-zA-Z].*")) {
			throw new AppException(ErrorCode.USER_ID_FORMAT, 422);
		}
		return userId;
	}

}
