package github.visual4.aacweb.dictation.service.mailing;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.TypeMap;


@Component
public class NcpMailingService {
	final private String endPoint = "https://mail.apigw.ntruss.com";
	
    @Value("${dictation.ncp.access-key}") private String accessKey;
    @Value("${dictation.ncp.secret-key}") private String secretKey;
    
    final ObjectMapper mapper;
    
    public NcpMailingService(ObjectMapper om) {
		this.mapper = om;
	}
    
    public void sendMail(Mail mail) {
    	String uri = "/api/v1/mails";
        String url = endPoint + uri;
        List<String> attachedFileIds = uploadAttachment(mail);
        
        TypeMap body = new TypeMap();
        body.put("senderAddress", mail.getSenderEmail());
        body.put("senderName", mail.getSenderName());
        body.put("advertising", "false");
        body.put("title", mail.getTitle());
        body.put("body", mail.getBody());
        
        List<TypeMap> recipients = new ArrayList<>();
        mail.forEachReceivers(rcv -> {
            TypeMap receiver = new TypeMap();
            receiver.put("address", rcv.mail);
            receiver.put("name", rcv.name);
            receiver.put("type", "R");
            recipients.add(receiver);
        });
        body.put("recipients", recipients);
        
        
        if (attachedFileIds.size() > 0) {
            body.put("attachFileIds", attachedFileIds);
        }
        
        Document doc = null;
        Response res = null;
        try {
            String jsonText = mapper.writeValueAsString(body);
            Connection con = Jsoup.connect(url);
            commonHeaders(con, uri);
            res = con
                .method(Method.POST)
                .header("Content-Type", "application/json; charset=UTF-8")
                .requestBody(jsonText)
                .execute();
            
            doc = res.parse();
            assertResponse(res.statusCode(), doc);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new AppException(ErrorCode.MAILING_ERROR, 500, "json error");
        } catch (IOException e) {
            e.printStackTrace();
            throw new AppException(ErrorCode.MAILING_ERROR, 500, "fail to send mail");
        }
    }
    
    private List<String> uploadAttachment(Mail mail) {
        List<Attachment> attachments = mail.getAttachments();
        if (attachments == null || attachments.size() == 0) {
            return Collections.emptyList();
        }
        String uri = "/api/v1/files";
        String url = endPoint + uri;
        Connection con = Jsoup.connect(url);
        con.method(Method.POST);
        commonHeaders(con, uri);
        for (Attachment attachment : attachments) {
            con.data("fileList", attachment.getFileName(), attachment.getInputStream(), attachment.getContentType());            
        }
        /*
         * ref
         * - https://apidocs.ncloud.com/ko/ai-application-service/cloud_outbound_mailer/create_file/
         * - https://apidocs.ncloud.com/ko/ai-application-service/cloud_outbound_mailer/create_mail_request/
         */
        List<String> fileIdList = new ArrayList<>();
        Document doc = null;
        Response res = null;
        try {
            res = con.execute();
            doc = res.parse();
            assertResponse(res.statusCode(), doc);
            
            String jsonText = doc.select("html > body").text().trim();
            
            @SuppressWarnings("unchecked")
            TypeMap responseBody = new TypeMap(mapper.readValue(jsonText, Map.class));
            
            List<TypeMap> files = responseBody.asList("files", (Map<String, Object> elem)-> new TypeMap(elem));
            for (TypeMap fileMeta : files) {
                fileIdList.add(fileMeta.getStr("fileId"));
            }
            return fileIdList;
        } catch (IOException e) {
            e.printStackTrace();
            throw new AppException(ErrorCode.SERVER_ERROR, 502, "MAILING_ERROR:ATTACHMENT");
        }
    }
    
    private Connection commonHeaders(Connection con, String uri) {
        long millis = System.currentTimeMillis();
        String signature = makeSignature(millis, accessKey, secretKey, uri);
        con
            .header("x-ncp-apigw-timestamp", "" + millis)
            .header("x-ncp-iam-access-key", accessKey)
            .header("x-ncp-apigw-signature-v2", signature);
        
        con.ignoreContentType(true).ignoreHttpErrors(true);
        return con;
    }

    private String makeSignature(long timestamp, String accessKey, String secretKey, String uri) {
        String method = "POST";
        String space = " ";
        String newLine = "\n";

        String message = new StringBuilder()
            .append(method)
            .append(space)
            .append(uri)
            .append(newLine)
            .append(timestamp)
            .append(newLine)
            .append(accessKey)
            .toString();

        SecretKeySpec signingKey = null;
        byte [] messageInBytes = null;
        try {
            signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
            messageInBytes = message.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new AppException(ErrorCode.MAILING_ERROR, 500, "encoding problem");
        }
        Mac mac = null;
        try {
            mac = Mac.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new AppException(ErrorCode.MAILING_ERROR, 500, "NO_SUCH_ALGORITHM:HmacSHA256");
        }
        try {
            mac.init(signingKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            throw new AppException(ErrorCode.MAILING_ERROR, 500, "INVALID_KEYSPEC");
        }

        byte[] rawHmac = mac.doFinal(messageInBytes);
        String encodeBase64String = Base64.encodeBase64String(rawHmac);
        return encodeBase64String;
    }
    
    private void assertResponse(int statusCode, Document doc) throws JsonMappingException, JsonProcessingException {
        String jsonText = doc.select("html > body").text().trim();
        @SuppressWarnings("unchecked")
        TypeMap res = new TypeMap(mapper.readValue(jsonText, Map.class));
        if (res.containsKey("error")) {
            TypeMap error = new TypeMap(res.get("error"));
            String errorCode = error.asStr("code");
            String message = error.asStr("message");
            throw new AppException(ErrorCode.MAILING_ERROR, statusCode, "Response:" + errorCode + "-" + message);
        }
    }
}
