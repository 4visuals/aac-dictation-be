package github.visual4.aacweb.dictation.web.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.user.UserService;
import github.visual4.aacweb.dictation.service.TokenService;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class AuthInfoInterceptor {

	final private static String BEARER = "Bearer ";
	
	final TokenService tokenService;
	final UserService userService;
	
    public AuthInfoInterceptor(TokenService tokenService, UserService userService) {
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void getMapping() {}
    
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void post() {}
    
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void put() {}
    
    @Pointcut("@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void del() {}
    
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void requestMapping() {}
    
    @Around("getMapping() || post() || put() || del()")
    public Object peekRequestParams(ProceedingJoinPoint jpoint) throws Throwable {
        return handle(jpoint);
    }

    private Object handle(ProceedingJoinPoint jpoint) throws Throwable {
        Object[] args = hasAnnotation(jpoint);
//        if () {
//            String authToken = readAuthorization();
//            TypeMap payload = tokenService.parseJwt(authToken);
//            AuthAccount account = userService.findBySeq(payload.asInt("seq"));
//            args[0] = account;
//            return jpoint.proceed(args);
//        }
//        else {
//            return jpoint.proceed();            
//        }
        return jpoint.proceed(args); 
    }

    private String readAuthorization() {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String token = req.getHeader("Authorization");
        if (token == null) {
            throw new AppException(ErrorCode.MISSING_AUTH_HEADER, 400);
        }
        if (token.startsWith(BEARER)) {
            return token.substring(BEARER.length());
        } else {
            throw new AppException(ErrorCode.MISSING_AUTH_HEADER, 400, "valid auth token required, but not found");
        }
    }

    private boolean hasType(Class<?> clazz, Object[] args, int i) {
        Object param = args[i];
        return  (param.getClass() == clazz);
    }
    private Object [] hasAnnotation(ProceedingJoinPoint jpoint) {
        Method method = MethodSignature.class.cast(jpoint.getSignature()).getMethod();
        Object[] args = jpoint.getArgs();
        Annotation[][] annotations = method.getParameterAnnotations();
        TypeMap payload = null;
        for(int ir = 0 ; ir < annotations.length; ir++) {
            JwtProp anno = findAnnotation(annotations[ir], JwtProp.class);
            if (anno != null) {
                String prop = anno.value();
                if (payload == null ){
                    String token = readAuthorization();
                    payload = tokenService.parseJwt(token);
                }
                if ("".equals(prop)) {
                	// @JwtProp TypeMap payload
                	args[ir] = payload;
                } else {
                	// @JwtProp("seq") Integer seq
                	args[ir] = payload.get(prop);                	
                }
            }
        }
        return args;
        // return  (param.getClass() == clazz);
    }

    private <T> T findAnnotation(Annotation[] annotations, Class<T> clazz) {
        for (int i = 0; i < annotations.length; i++) {
            if (annotations[i] instanceof JwtProp) {
                return (T)annotations[i];
            }
        }
        return null;
    }
}
