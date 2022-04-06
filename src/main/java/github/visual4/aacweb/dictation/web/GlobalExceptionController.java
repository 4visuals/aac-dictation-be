package github.visual4.aacweb.dictation.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.TypeMap;

@ControllerAdvice
public class GlobalExceptionController {

	@ExceptionHandler(AppException.class)
	public Object handleAppException(AppException e) {
		String code = e.getErrorCode();
        HttpStatus status = codeToStatus(e.getHttpCode());
        TypeMap body = TypeMap.with("cause", code, "desc", e.getDetail());
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
