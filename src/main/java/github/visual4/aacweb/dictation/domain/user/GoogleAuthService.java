package github.visual4.aacweb.dictation.domain.user;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import javax.annotation.PostConstruct;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GooglePublicKeysManager;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.Util;

@Service
@Qualifier("google")
public class GoogleAuthService {

	final String clientId;
    private GooglePublicKeysManager keyManager;
	ObjectMapper om;
    
	public GoogleAuthService(@Value("${ouath.google.client-id}")  String clientId, ObjectMapper om) {
		this.clientId = clientId;
		this.om = om;
	}
	
	@PostConstruct
    public void init() {
        HttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = new GsonFactory();
        keyManager = new GooglePublicKeysManager(transport, jsonFactory);
    }
	private Connection createConnection(String url, Object ... props) {
		if (props.length % 2 == 1) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500, "# of props is odd(should be [key, value, key, value, ...]");
		}
		Connection con = Jsoup.connect(url);
		con.ignoreContentType(true);
		for(int i = 0 ; i < props.length; i += 2) {
			con.data(props[i].toString(), props[i+1].toString());
		}
		return con;
	}
	private TypeMap parseJson(String json) {
		TypeMap res = Util.parseJson(om, json);
		res.put("vendor", "google");
		return res;
	}
	/**
	 * access_token으로 사용자 정보 조회
	 * @param accessToken
	 * @return map of {sub=, email_verified=boolean, name=string, given_name=string, locale=ko, family_name=string, picture=url, email=}
	 */
	public TypeMap getUserProfile(String accessToken) {
		// https://www.googleapis.com/oauth2/v3/userinfo?access_token=ya29.A0ARrdaM_ywc3yHF8NYGGya0BBcA4ZVnIiNbhA_whuu3iQAA1nIhbnVPLk0x41q2qCv-VFUyl-oWfZo5bVx9FglIr2JZaLofEjX4EJwjWp_aF2EX0KQ32jNkxxr6VX7mJIE9CkcU3VRY7NSrfPVEFTD5BE5jzajg
		String url = "https://www.googleapis.com/oauth2/v3/userinfo";
		Connection con = createConnection(url, "access_token", accessToken);
		try {
			Document doc = con.get();
			return parseJson(doc.select("html body").text().trim());
		} catch (IOException e) {
			e.printStackTrace();
			throw new AppException(ErrorCode.OAUTH_EXCEPTION, 500, "GOOGLE");
		}
	}


	/**
	 * 구글 계정으로 로그인 후 얻어낸 id_token 으로 사용자 정보 확인 
	 * 
	 * @param params - {"token": id_token to verify user}
	 * @return - {vendor: 'GOOGLE', 'name': 사용자 이름, 'email': 사용자 이메일, 'verified': boolean, 'picture': picture url}
	 */
	public TypeMap getUserProfile(TypeMap params) {
		/*
         * token debugging url
         * https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=[id_token_here]
         * https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=4/0AX4XfWjJD-9e3W6cuyL0gWNcdDCGoWoLhtdeHpkg-Mo0K3zy7S-2AMHEYnJdUvFL7HXtEw
         * 
         */
        GoogleIdTokenVerifier tokenVerifier = new GoogleIdTokenVerifier.Builder(keyManager)
                .setAudience(Collections.singletonList(clientId))
                .build();

        GoogleIdToken token = null;
        String idToken = params.getStr("token");
        try {
            token = tokenVerifier.verify(idToken);
        } catch (GeneralSecurityException | IOException e) {
           throw new AppException("SERVER_ERROR", 500, "fail to parse id_token(from google) cause: %s", e.getClass().getName());
        }
        
        Payload load = token.getPayload();
        
        String email = load.getEmail();
        boolean emailVerified = Boolean.valueOf(load.getEmailVerified());
        String name = (String) load.get("name");
        String pictureUrl = (String) load.get("picture");
        String locale = (String) load.get("locale");
        String familyName = (String) load.get("family_name");
        String givenName = (String) load.get("given_name");
        
        TypeMap res = TypeMap.with(
                "vendor", "GOOGLE",
                "name", name,
                "email", email,
                "verified", emailVerified,
                "picture", pictureUrl);
        return res;
	}
}
