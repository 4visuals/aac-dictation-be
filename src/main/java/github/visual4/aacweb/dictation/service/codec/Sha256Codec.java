package github.visual4.aacweb.dictation.service.codec;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;

@Component
@Qualifier("sha256")
public class Sha256Codec implements ICodec<String, String> {

	private static final String ALGO_NAME = "SHA-256";
	private static char [][] CODES = new char[16*16][2];
	
	public Sha256Codec() {
		// for fast fail
		init();
		initCode();
	}
	
	private void initCode() {
		char [] pattern = "0123456789ABCDEF".toCharArray();
		for(int i = 0 ; i < 256 ; i++) {
			int p0 = i / 16;
			int p1 = i % 16;
			CODES[i][0] = pattern[p0];
			CODES[i][1] = pattern[p1];
		}
		
	}

	private MessageDigest init() {
		try {
			return MessageDigest.getInstance(ALGO_NAME);
		} catch (NoSuchAlgorithmException e) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500, "fail to create SHA-256 instance.");
		}
	}
	@Override
	public String encode(String plainSource) {
		
		MessageDigest dg = init();
		dg.update(plainSource.getBytes(Charset.forName("UTF-8")));
		byte [] e = dg.digest();
		return toHex(e);
	}

	private String toHex(byte[] data) {
		char [] hex = new char[data.length*2];
		for (int i = 0; i < data.length; i++) {
			int b = data[i] < 0 ? 256 + data[i] : data[i];
			hex[2*i+0] = CODES[b][0];
			hex[2*i+1] = CODES[b][1];
		}
		return new String(hex);
	}

	@Override
	public String decode(String encodedSource) {
		throw new AppException(ErrorCode.APP_BUG, 500, "SHA-256 does not provide decoder.");
	}

}
