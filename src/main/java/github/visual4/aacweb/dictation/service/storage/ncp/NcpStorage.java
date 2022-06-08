package github.visual4.aacweb.dictation.service.storage.ncp;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.service.storage.IFileStorage;
import github.visual4.aacweb.dictation.service.storage.INameResolver;
import github.visual4.aacweb.dictation.service.storage.IUpfile;
import lombok.extern.slf4j.Slf4j;
@Component
@Slf4j
public class NcpStorage implements IFileStorage {

	final String endPoint = "https://kr.object.ncloudstorage.com";
	final String regionName = "kr-standard";
	
	@Value("${dictation.ncp.storage.access-key}") String accessKey;
	@Value("${dictation.ncp.storage.secret-key}") String secretKey;
	@Value("${dictation.ncp.storage.bucket}") String bucketName;
	
	private AmazonS3 s3;
	
	@PostConstruct
	public void init() {
		
		s3 = AmazonS3ClientBuilder.standard()
				.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
				.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
				.build();
	}
	@Override
	public void upload(IUpfile file, INameResolver resolver) {
		String objectName = resolver.resolveFileName(file);
		int flen = file.getFileLength();
		try {
			ObjectMetadata headers = new ObjectMetadata();
			headers.setContentLength(flen);
			headers.setContentType(file.getContentType());
			headers.setContentDisposition("filename=\"" + encode(file.getFileName()) + "\"");
			InputStream fis = file.openStream();
			PutObjectRequest req = new PutObjectRequest(bucketName, objectName, fis, headers)
					.withCannedAcl(CannedAccessControlList.PublicRead);
			
			PutObjectResult res = s3.putObject(req); 
			log.info("#SUCCESS " + file.getFileName());
		} catch (AmazonS3Exception e) {
			e.printStackTrace();
		    throw new AppException(ErrorCode.SERVER_ERROR, 500, "FAIL_TO_UPLOAD");
		} catch(SdkClientException e) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500, "FAIL_TO_UPLOAD");
		} catch (IOException e) {
			e.printStackTrace();
			throw new AppException(ErrorCode.SERVER_ERROR, 500, "FAIL_TO_UPLOAD");
		}
	}
	private String encode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8").replaceAll("\\+", "%20");
		} catch (UnsupportedEncodingException e) {
			return "no-filename." + ext(s);
		}
	}
	
	private String ext(String filename) {
		int i = filename.indexOf('.');
		String ext = filename.substring(i+1).toLowerCase();
		return ext;
	}
	@Override
	public void deleteFile(String path) {
		/*
		 * 앞에 slash(/) 있으면 지우지 못함
		 */
		String key = path.startsWith("/") ? path.substring(1) : path;
		try {
//			s3.deleteObj
		    s3.deleteObject(bucketName, key);
		    log.info("[NCP][DELETE] {}", key);
		} catch (AmazonS3Exception e) {
		    e.printStackTrace();
		} catch(SdkClientException e) {
		    e.printStackTrace();
		}
	}

}
