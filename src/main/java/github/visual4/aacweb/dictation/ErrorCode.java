package github.visual4.aacweb.dictation;

public enum ErrorCode {
    /**
     * 요청에 Authorization header가 없음
     */
    MISSING_AUTH_HEADER("valid auth token required, but not found"),
    PROP_NOT_UPDATABLE("property %s is not allowed to update"),
    /**
     * 부적합한 입력(null 공백 등)
     */
    INVALID_VALUE("invalid value found"),
    /**
     * 계정 정보가 없음(로그인 실패 등)
     */
    NOT_A_MEMBER("not a member"),
    /**
     * 이미 존재하는 리소스
     */
    DUP_RESOURCE("duplicated resource: %s"),
    /**
     * 없는 리소스
     */
    NOT_FOUND("resource not found %s"), 
    /**
     * 검증값이 일치하지 않음
     */
    VALUE_MISMATCH("value mismatched: %s"),
    SERVER_ERROR("server error: %s"),
    /**
     * jwt token signiture 값이 이상함
     */
    BAD_TOKEN_SIGNITURE("invalid token signiture: %s"),
    /**
     * 시간 만료됨
     */
    TOKEN_EXPIRED("token expired: %s"),
    OAUTH_EXCEPTION("[%s] oauth request faield."), 
    EXISTING_USER("user exists"),
    /**
     * 교사가 아님
     */
    NOT_A_TEACHER("not a teacher"),
    /**
     * 학생이 아님
     */
    NOT_A_STUDENT("not a student"),
    /**
     * 관리자 아님
     */
    NOT_A_ADMIN("not a admin account"),
    /**
     * 애플리케이션 버그 발생
     */
    APP_BUG("application bug. %s"),
    LICENSE_EXPIRED("expired license: %s"),
    LICENSE_IS_FULL("license is full: %s"), 
    LICENSE_PROBLEM("this license has problem. %s"),
    LICENSE_ALREADY_ACTIVATED("This license already activated: student [%d]"),
    NOT_A_LICENSE_OWNER("not a license owner"), 
    VOICE_ERROR("fail to speak: voice: [%s], text[%s]"), 
    STUDENT_NOT_ENROLLED("no license found for student [%s]"),
    STUDENT_LOGIN_FAILED("id or password mismatch"), 
    NOT_YOUR_STUDENT("not your student"), 
    
    DUP_USER_ID("duplicated user id"),
    NULL_USER_NAME("empty user name"),
    DUP_USER_EMAIL("duplicated user email"),
    NULL_USER_EMAIL("empty user email"),
    PASSWORD_LENGTH("empty password"),
    
    OUT_OF_LENGTH("check the size [%s]"), 
    PASSWORD_FORMAT("number and character is required."), 
    USER_ID_LENGTH("length should be between 6 and 16."), 
    USER_ID_FORMAT("character  is required"),
    USER_NAME_LENGTH("check the name size(1 ~ 12)"),
    USER_EMAIL_FORMAT("check email format"),
    
    NOT_ALLOWED("this is not allowed"), 
    
    ORDER_INVALID_QTT("valid quantity required"), 
    ORDER_INVALID_PROD("no such product: %s"), 
    ORDER_INVALID_PRICE("null or negative price"),
    ORDER_ALREADY_ACTIVATED("already activated"),
    
    PRODUCT_ERROR("product problem: %s"),
    SETTING_UNLOCK_FAILED("password mismatch")
    ;
    

    private String format;

    ErrorCode(String format) {
        this.format = format;
    }
    public String detail(Object ... args) {
        return String.format(format, args);
    }
    
    
}

