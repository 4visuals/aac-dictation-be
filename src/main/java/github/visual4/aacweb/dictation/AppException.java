package github.visual4.aacweb.dictation;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AppException extends RuntimeException{

    private static final long serialVersionUID = -245311703788879481L;
    private String errorCode;
    private int httpCode;
    private String detail;
    
    public AppException(String errorCode, int httpCode, String detail) {
        this.errorCode = errorCode;
        this.httpCode = httpCode;
        this.detail = detail;
    }
    
    public AppException(String errorCode, int httpCode) {
        this.errorCode = errorCode;
        this.httpCode = httpCode;
        this.detail = errorCode + " (http response " + httpCode + ")";
    }

    public AppException(String errorCode, int httpCode, String format, Object ... params ) {
        this.errorCode = errorCode;
        this.httpCode = httpCode;
        this.detail = String.format(format, params);
    }

    public AppException(ErrorCode error, int httpCode, Object ... params) {
        this.errorCode = error.name();
        this.httpCode = httpCode;
        this.detail = error.detail(params);
    }
    
}
