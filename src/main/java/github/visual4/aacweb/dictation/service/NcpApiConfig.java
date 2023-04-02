package github.visual4.aacweb.dictation.service;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;

public class NcpApiConfig {

	private static String makeSignature(
			String method,
			long millis,
			String accessKey,
			String secretKey,
			String uri) {
//        String method = "POST";
        String space = " ";
        String newLine = "\n";
        /*
         =====
         POST https://https://mail.apigw.ntruss.com
         [TIMESTAMP]
         [ACCESS KEY]
         =====
         */
        String message = new StringBuilder()
            .append(method)
            .append(space)
            .append(uri)
            .append(newLine)
            .append(millis)
            .append(newLine)
            .append(accessKey)
            .toString();

        SecretKeySpec signingKey = null;
        byte [] messageInBytes = null;
        try {
            signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
            messageInBytes = message.getBytes("UTF-8");
            Mac mac = null;
            mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(messageInBytes);
            String encodeBase64String = Base64.encodeBase64String(rawHmac);
            return encodeBase64String;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new AppException(ErrorCode.SERVER_ERROR, 500, "fail to send api request");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new AppException(ErrorCode.SERVER_ERROR, 500, "NO_SUCH_ALGORITHM:HmacSHA256");
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            throw new AppException(ErrorCode.SERVER_ERROR, 500, "INVALID_KEYSPEC");
        }

    }
	
	public static Map<String, String> getHeaders(String method, String accessKey, String secretKey, String uri) {
		long millis = System.currentTimeMillis();
		String signature = makeSignature(method, millis, accessKey, secretKey, uri);
		Map<String, String> headers = new HashMap<>();
		
        headers.put("x-ncp-apigw-timestamp", "" + millis);
        headers.put("x-ncp-iam-access-key", accessKey);
        headers.put("x-ncp-apigw-signature-v2", signature);
        return headers;
	}
	
	
}
