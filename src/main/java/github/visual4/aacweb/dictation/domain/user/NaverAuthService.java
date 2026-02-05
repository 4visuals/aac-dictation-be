package github.visual4.aacweb.dictation.domain.user;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.Util;

@Service
@Qualifier("naver")
public class NaverAuthService {

	private static final String TOKEN_URL = "https://nid.naver.com/oauth2.0/token";
	private static final String PROFILE_URL = "https://openapi.naver.com/v1/nid/me";

	private final String clientId;
	private final String clientSecret;
	private final String state;
	private final ObjectMapper om;

	public NaverAuthService(
			@Value("${ouath.naver.client-id}") String clientId,
			@Value("${ouath.naver.client-secret}") String clientSecret,
			@Value("${ouath.naver.state:}") String state,
			ObjectMapper om) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.state = state;
		this.om = om;
	}

	private Connection createConnection(String url, Object... props) {
		if (props.length % 2 == 1) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500,
					"# of props is odd(should be [key, value, key, value, ...]");
		}
		Connection con = Jsoup.connect(url);
		con.ignoreContentType(true);
		for (int i = 0; i < props.length; i += 2) {
			con.data(props[i].toString(), props[i + 1].toString());
		}
		return con;
	}

	private TypeMap parseJson(String json) {
		return Util.parseJson(om, json);
	}

	public TypeMap issueAccessToken(String code, String stateOverride) {
		Connection con = createConnection(
				TOKEN_URL,
				"grant_type", "authorization_code",
				"client_id", clientId,
				"client_secret", clientSecret,
				"code", code);
		String stateValue = stateOverride;
		if (stateValue == null || stateValue.isBlank()) {
			stateValue = state;
		}
		if (stateValue != null && !stateValue.isBlank()) {
			con.data("state", stateValue);
		}
		try {
			Document doc = con.get();
			TypeMap res = parseJson(doc.select("html body").text().trim());
			if (res.get("error") != null) {
				String error = res.getStr("error");
				String errorDescription = res.getStr("error_description");
				String detail = errorDescription == null ? error : error + " (" + errorDescription + ")";
				throw new AppException(ErrorCode.OAUTH_EXCEPTION, 500, "NAVER: " + detail);
			}
			return res;
		} catch (IOException e) {
			throw new AppException(ErrorCode.OAUTH_EXCEPTION, 500, "NAVER");
		}
	}

	public TypeMap getUserProfile(String accessToken) {
		Connection con = Jsoup.connect(PROFILE_URL);
		con.ignoreContentType(true);
		con.header("Authorization", "Bearer " + accessToken);
		try {
			Document doc = con.get();
			TypeMap payload = parseJson(doc.select("html body").text().trim());
			return toProfile(payload);
		} catch (IOException e) {
			throw new AppException(ErrorCode.OAUTH_EXCEPTION, 500, "NAVER");
		}
	}

	private TypeMap toProfile(TypeMap payload) {
		String resultCode = payload.getStr("resultcode");
		if (resultCode != null && !"00".equals(resultCode)) {
			throw new AppException(ErrorCode.OAUTH_EXCEPTION, 500, "NAVER");
		}
		Object response = payload.get("response");
		if (!(response instanceof Map)) {
			throw new AppException(ErrorCode.OAUTH_EXCEPTION, 500, "NAVER");
		}
		@SuppressWarnings("unchecked")
		TypeMap profile = new TypeMap((Map<String, Object>) response);
		if (profile.get("profile_image") != null && profile.get("picture") == null) {
			profile.put("picture", profile.get("profile_image"));
		}
		String name = profile.getStr("name");
		if (name == null || name.isBlank()) {
			String nickname = profile.getStr("nickname");
			if (nickname != null && !nickname.isBlank()) {
				profile.put("name", nickname);
			}
		}
		profile.put("vendor", "naver");
		profile.put("resultcode", resultCode);
		profile.put("message", payload.getStr("message"));
		return profile;
	}
}
