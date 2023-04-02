package github.visual4.aacweb.dictation.service.sms;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.Util;
import github.visual4.aacweb.dictation.service.NcpApiConfig;

@Component
public class NcpSmsService {

	final private String accessKey;
	final private String secretKey;
	final private String smsServiceId;
	final private String smsHost = "https://sens.apigw.ntruss.com";
	final private String smsUri="/sms/v2/services/{serviceId}/messages";
	final ObjectMapper om;
	final private String smsSender;
	public NcpSmsService(
			@Value("${dictation.ncp.sms.access-key}") String accessKey,
			@Value("${dictation.ncp.sms.secret-key}") String secretKey,
			@Value("${dictation.ncp.sms.service-id}")String smsServiceId,
			@Value("${dictation.ncp.sms.sender-phone-number}")String smsSender,
			ObjectMapper om) {
		super();
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.smsServiceId = smsServiceId;
		this.om = om;
		this.smsSender = smsSender;
	}

	public TypeMap sendSms(String phoneNum, String text) {
		String uri = smsUri.replace("{serviceId}", smsServiceId);
		TypeMap body = TypeMap.with(
				"type", "SMS",
				"contentType", "COMM",
				"from", this.smsSender.replaceAll("[^0-9]", ""),
				"content", text,
				"messages", Arrays.asList(
					TypeMap.with("to", phoneNum.replaceAll("[^0-9]", ""))
				)
		);
		
		Map<String, String> headers = NcpApiConfig.getHeaders(
				Method.POST.name(),
				accessKey,
				secretKey, uri);
		Response res = null;
		try {
			
			String url = smsHost + uri;
			Connection con = Jsoup.connect(url);
			con.method(Method.POST);
			con.ignoreContentType(true).ignoreHttpErrors(true);
			for(String headerName : headers.keySet()) {
				String value = headers.get(headerName);
				con.header(headerName, "" + value);
//				System.out.printf("[HEADER](%s):(%s)\n", headerName, value);
			}
			con.header("Content-Type", "application/json; charset=UTF-8");
			String jsonText = Util.stringify(om, body);
			con.requestBody(jsonText);
			res = con.execute();
			return assertResponse(res);
		} catch (IOException e) {
			throw new AppException(ErrorCode.SMS_ERROR, 500, "fail to send sms: " + e.getMessage());
		}
	}

	private TypeMap assertResponse(Response res) throws IOException {
		/*
		 * {
		 * 	"statusCode":"202",
		 * 	"statusName":"success",
		 * 	"requestId":"VORSSA-1...",
		 *  "requestTime":"2023-03-31T15:47:58.269"
		 * }
		 */
		Document doc = res.parse();
		String bodyJson = doc.select("html > body").text().trim();
		/*
		 * {"errors":
		 *     ["The receiver(to) must be numeric and cannot exceed 25 characters."]
		 *  "errorMessage":
		 *      "Validation failed. 1 error(s)",
		 *   "status":400}
		 */
		TypeMap body = Util.parseJson(om, bodyJson);
		String statusCode = body.asStr("statusCode");
		if (!"202".equals(statusCode)) {
			System.out.println(body); 
			Object errors = body.get("errors");
			throw new AppException(ErrorCode.SMS_ERROR, 400, errors == null ? "": errors.toString());
		}
		return body;
		
	}
}
