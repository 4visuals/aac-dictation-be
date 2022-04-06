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
     * note의 소유자가 아님
     */
    NOT_A_NOTE_OWNER("not a owner of note"),
    /**
     * 계정 정보가 없음(로그인 실패 등)
     */
    NOT_A_MEMBER("not a member"),
    /**
     * collection의 소유자가 아님
     */
    NOT_A_COLLECTION_OWNER("not a owner of collection"),
    /**
     * 카테고리 소유자가 아님
     */
    NOT_A_CATE_OWNER("not a owner of category"),
    /**
     * 사건 소유자가 아님
     */
    NOT_A_EVENT_OWNER("not a owner of event"),
    /**
     * 책 소유자 아님
     */
    NOT_A_BOOK_OWNER("not a owner of book %s"),
    /**
     * 이미 존재하는 리소스
     */
    DUP_RESOURCE("duplicated resource %s"),
    /**
     * 경로 충돌(부모 카테고리를 자손 카테고리로 이동시키려함 
     */
    CATE_CIRCULAR_PATH("cannot move parent to child"), 
    /**
     * 사건 사이에 인과 관계 정보가 없음
     */
    EVENT_EDGE_NOT_FOUND("no edge from event(%d) to event(%d)"),
    /**
     * 사건 edge 오류
     */
    EVENT_EDGE_ERROR("%s"),
    /**
     * 사건 연결에서 graph 발생
     */
    CIRCURAL_EDGE_ERROR("circual edge not allowed"),
    /**
     * 없는 리소스
     */
    NO_FOUND("resource not found %s"), 
    /**
     * 검증값이 일치하지 않음
     */
    VALUE_MISMATCH("value mismatched: %s"),
    SERVER_ERROR("server error: %s"),
    /**
     * jwt token signiture 값이 이상함
     */
    BAD_TOKEN_SIGNITURE("invalid token signiture: %s"),
    OAUTH_EXCEPTION("[%s] oauth request faield."), 
    
    EXISTING_USER("user exists");
    

    private String format;

    ErrorCode(String format) {
        this.format = format;
    }
    public String detail(Object ... args) {
        return String.format(format, args);
    }
    
    
}

