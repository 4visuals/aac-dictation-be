package github.visual4.aacweb.dictation.config;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.Util;
import github.visual4.aacweb.dictation.service.codec.RsaCodec;
import github.visual4.aacweb.dictation.service.tts.ITtsHandler;
import github.visual4.aacweb.dictation.service.tts.PollyTtsHandler;
import lombok.extern.slf4j.Slf4j;


@Configuration
@EnableScheduling
@Slf4j
public class AacDictationConfig implements WebMvcConfigurer{

	@Value("${dictation.cors.allowed-urls}")
    String [] corsAllowedUrls;
	
	@Value("${dictation.mode}") String mode;
	@Value("${dictation.version}") String version;
	
	@Value("${spring.datasource.url}") String dbUrl;
	
	@Value("${dictation.rsa.priv.file}")
	String rsaPrivPath;
	
	@Value("${dictation.rsa.public.file}")
	String rsaPublicPath;
	
	
	@PostConstruct
	public void info() {
		System.out.printf(" * [MODE] %s(version: %s)\n", mode, version);
		System.out.printf(" * [CORS] %s\n", Arrays.toString(corsAllowedUrls));
		System.out.printf(" * [DB  ] %s\n", dbUrl);
	}
	
	@Bean
	public ObjectMapper om() {
		ObjectMapper om = new ObjectMapper();
		om.registerModule(new JavaTimeModule())
			.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
			.setDateFormat(new StdDateFormat())
			;
		om.setSerializationInclusion(Include.NON_NULL);
		return om;
	}
	@Bean
    @Qualifier("md5")
    public MessageDigest md5() {
    	try {
			return MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500, "fail to create a md5 instance");
		}
    }
	
	@Bean
	public RsaCodec rsaCodec() {
		Charset utf8 = Charset.forName("utf-8");
		String privContent = Util.readFile(rsaPrivPath);
		privContent = privContent
				.replace("-----BEGIN PRIVATE KEY-----\n", "")
				.replace("\n-----END PRIVATE KEY-----", "");
		ByteArrayInputStream privStream = new ByteArrayInputStream(privContent.getBytes(utf8));
		
		String pubContent = Util.readFile(rsaPublicPath);
		pubContent = pubContent
				.replace("-----BEGIN PUBLIC KEY-----\n", "")
				.replace("\n-----END PUBLIC KEY-----", "");
		ByteArrayInputStream pubStream = new ByteArrayInputStream(pubContent.getBytes(utf8));
		
		return new RsaCodec(privStream, pubStream);
	}
	
	@Bean
	public ITtsHandler amazonPollyTtsHandler(
			@Value("${dictation.aws.access-key}") String awsAccessKey,
			@Value("${dictation.aws.secret-key}") String awsSecretKey) {
		return new PollyTtsHandler(awsAccessKey, awsSecretKey);
	}
	
	@Override
    public void addCorsMappings(CorsRegistry reg) {
        log.info("[CORS CONFIG] {}", Arrays.toString(corsAllowedUrls));
        reg
            .addMapping("/**")
            .allowedOrigins(corsAllowedUrls)
            .allowedMethods(
                    HttpMethod.GET.name(),
                    HttpMethod.HEAD.name(),
                    HttpMethod.POST.name(),
                    HttpMethod.PUT.name(),
                    HttpMethod.DELETE.name()
                    );
    }
}
