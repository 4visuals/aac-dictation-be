package github.visual4.aacweb.dictation.web;

import java.sql.SQLException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.TypeMap;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionController {

	@ExceptionHandler(AppException.class)
	public Object handleAppException(AppException e) {
		String code = e.getErrorCode();
        HttpStatus status = codeToStatus(e.getHttpCode());
        TypeMap body = TypeMap.with("cause", code, "desc", e.getDetail());
		return new ResponseEntity<>(body, status);
	}
	
	@ExceptionHandler(SQLException.class)
	public Object handleSqlException(SQLException e) {
		String code = ErrorCode.SERVER_ERROR.name();
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		String state = e.getSQLState();
		String msg = e.getMessage();
		log.error("[DB ERROR] state({}), message({})", state, msg);
		TypeMap body = TypeMap.with("cause", code, "desc", "state:" + state);
		return new ResponseEntity<>(body, status);
	}
	private HttpStatus codeToStatus(int resCode) {
        for(HttpStatus status : HttpStatus.values()) {
            if (status.value() == resCode) {
                return status;
            }
        }
        return HttpStatus.BAD_REQUEST;
    }
	
}
