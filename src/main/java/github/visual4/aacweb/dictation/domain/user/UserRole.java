package github.visual4.aacweb.dictation.domain.user;

import java.util.HashMap;
import java.util.Map;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;

public enum UserRole {

	TEACHER("T"),
	STUDENT("S");

	private String code;
	private static Map<String, UserRole> map;
    static {
        map = new HashMap<>();
        for(UserRole role : UserRole.values()) {
            map.put(role.code, role);
        }
    }

	UserRole(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}

	public static UserRole fromCode(String roleCode) {
		UserRole role = map.get(roleCode);
		if (role == null) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500, "invalid roleCode. \"T\" or \"S\" is allowed but [" + roleCode + "]");
		}
		return role;
	}
}
