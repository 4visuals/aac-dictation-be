package github.visual4.aacweb.dictation.service.rule;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;

public class UserNameRule implements IRule {

	@Override
	public String checkRule(String name) {
		if (name.length() > 12) {
			throw new AppException(ErrorCode.USER_NAME_LENGTH, 422);
		}
		return name;
	}

}
