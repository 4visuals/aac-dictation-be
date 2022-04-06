package github.visual4.aacweb.dictation.service.codec;

import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;

@Component
@Qualifier("aes256")
public class Aes256Codec implements ICodec<String, String> {

	final Cipher encoder;
    final Cipher decoder ;
    public Aes256Codec(
    		@Value("${dictation.aes256.salt}") String saltKey,
    		MessageDigest md5) {
    	byte [] key = new byte[32];
    	byte [] iv = new byte[16];
    	byte [] salt = saltKey.getBytes();
     	generateKey(md5, salt, key, 4);
    	generateKey(md5, key, iv, 4);
    	
    	SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
    	IvParameterSpec ivParamSpec = new IvParameterSpec(iv);
    	
    	String algorithm = "AES/CBC/PKCS5Padding";
		try {
			encoder = Cipher.getInstance(algorithm);
			encoder.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);
			
			decoder = Cipher.getInstance(algorithm);
			decoder.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);
		} catch (Exception e) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500, e.getClass().getName());
		}
        
    }
    
	private void generateKey(MessageDigest hasher, byte [] salt, byte[] out, int chunk) {
		int offset = 0;
		hasher.reset();
		byte [] block = null;
		while (offset < out.length) {
			hasher.update(salt);
			block = hasher.digest();
			System.arraycopy(block, 0, out, offset, chunk);
			hasher.update(block);
			offset += chunk;
		}
	}
	@Override
	public String encode(String src) {
		try {
			byte[] out = encoder.doFinal(src.getBytes("UTF-8"));
			return Base64.getEncoder().encodeToString(out);
		} catch (Exception e) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500,
					"[Aes256Codec.class] encoding error. cause: " + e.getClass().getName());
		}
	}

	@Override
	public String decode(String cipherText) {
		try {
			byte [] cipher = Base64.getDecoder().decode(cipherText);
			byte[] plain;
			plain = decoder.doFinal(cipher);
			return new String(plain, "UTF-8");
		} catch (IllegalArgumentException e) {
			//  만료된 토크을 decoding 할때 오류 발생
			throw new AppException(ErrorCode.BAD_TOKEN_SIGNITURE, 400, "not a base64 text: " + e.getMessage());
		} catch (Exception e) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500,
					"[Aes256Codec.class] decoding error. cause: " + e.getClass().getName());
		}
		
	}

}
