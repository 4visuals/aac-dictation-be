package github.visual4.aacweb.dictation.service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.user.UserRole;
import github.visual4.aacweb.dictation.service.codec.ICodec;
import github.visual4.aacweb.dictation.service.codec.RsaCodec;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TokenService {
    final String host;
	final ObjectMapper om;
    static final long ONE_HOUR_MILLIS = 60L * 60 * 1000;
	final ICodec<String, String> aes256;
	final RsaCodec rsa;
	
	public TokenService(
			@Value("${dictation.host}") String host,
    		ObjectMapper om,
    		@Qualifier("aes256") ICodec<String, String> codec,
    		RsaCodec rsaCodec) {
		this.host = host;
		this.om = om;
		this.aes256 = codec;
		this.rsa = rsaCodec;
	}
	/**
     * jtw token 발급
     * @param props
     * @return
     */
    public String generateJwt(TypeMap props, UserRole role) {
        Date current = new Date();
        TypeMap headers = TypeMap.with("typ", "JWT");
        
        props.put("role", role);
        TypeMap claim = TypeMap.copy(props);
        JwtBuilder builder = Jwts.builder()
            .setHeader(headers)
            .setExpiration(new Date(current.getTime() + 12 * ONE_HOUR_MILLIS))
            .setIssuedAt(current)
            .setIssuer(host)
            .addClaims(claim);
        return builder
        		.signWith(SignatureAlgorithm.RS256, rsa.getPrivateKey())
                .compact();
    }
    
    public TypeMap parseJwt(String jwtToken) {
        try {
            Jws<Claims> res = Jwts
            		.parser()
            		.setSigningKey(rsa.getPublicKey())
            		.parseClaimsJws(jwtToken);
            log.debug("[JWT' {}", res);
            Claims claim = res.getBody();
            TypeMap body =  new TypeMap(claim);
            UserRole role =  UserRole.valueOf(body.getStr("role"));
            body.put("role", role); // convert "TEACHER" -> UserRole.TEACHER
            return body;
        } catch (ExpiredJwtException e) {
            String details = e.getMessage();
            throw new AppException(ErrorCode.TOKEN_EXPIRED, 410, details);
        } catch (SignatureException e) {
        	String details = e.getMessage();
        	throw new AppException(ErrorCode.BAD_TOKEN_SIGNITURE, 410, details);
        }
    }
}
