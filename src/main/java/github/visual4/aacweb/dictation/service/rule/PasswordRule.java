package github.visual4.aacweb.dictation.service.rule;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;

public class PasswordRule implements IRule {

	@Override
	public String checkRule(String pass) {
		if (pass == null || pass.length() < 6 || pass.length() > 16) {
			throw new AppException(ErrorCode.PASSWORD_LENGTH, 409);
		}
		if (!pass.matches(".*[0-9].*")) {
			throw new AppException(ErrorCode.PASSWORD_FORMAT, 409);
		}
		if (!pass.matches(".*[a-zA-Z].*")) {
			throw new AppException(ErrorCode.PASSWORD_FORMAT, 409);
		}
		return pass;
	}
}
