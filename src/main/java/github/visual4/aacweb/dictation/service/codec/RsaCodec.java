package github.visual4.aacweb.dictation.service.codec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
/**
 * key 생성
 * 
 * $ openssl genrsa -out jwt.pem 2048
 * $ openssl pkcs8 -topk8 -in jwt.pem -inform PEM -out aacdict_jwt  -outform PEM  -nocrypt
 * $ openssl rsa -in aacdict_jwt -pubout -outform PEM -out aacdict_jwt.pub
 * $ ls -al
 * drwxr-xr-x   5 [skip]  staff   160 Apr  1 20:03 .
 * drwxr-xr-x  11 [skip]  staff   352 Apr  1 18:52 ..
 * -rw-r--r--   1 [skip]  staff  1704 Apr  1 20:03 aacdict_jwt     (RSA private key)
 * -rw-r--r--   1 [skip]  staff   451 Apr  1 20:03 aacdict_jwt.pub (RSA public key)
 * -rw-r--r--   1 [skip]  staff  1679 Apr  1 20:02 jwt.pem
 * @author chminseo
 *
 */
public class RsaCodec implements ICodec<String, String> {

	private PrivateKey privKey;
	private PublicKey pubKey;

	public RsaCodec(InputStream privStream, InputStream pubStream) {
		KeyFactory fac;
		try {
			fac = KeyFactory.getInstance("RSA");
			privKey = loadPrivateKey(fac, privStream);
			pubKey = loadPublickKey(fac, pubStream);
			
		} catch (NoSuchAlgorithmException e) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500, "no such algorithm : RSA");
		}
	}
	
	public PrivateKey getPrivateKey() {
		return privKey;
	}
	public PublicKey getPublicKey() {
		return pubKey;
	}

	private PrivateKey loadPrivateKey(KeyFactory fac, InputStream in) {
		byte[] data;
		try {
			data = readToBytes(in);
			byte [] decoded = Base64.getMimeDecoder().decode(data);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
			return fac.generatePrivate(keySpec);
		} catch (IOException e) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500, "fail to read RSA priv key");
		} catch (InvalidKeySpecException e) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500, "invalid key spec while generating RSa priv key");
		}
	}
	private PublicKey loadPublickKey(KeyFactory fac, InputStream in) {
		byte [] data;
		try {
			data = readToBytes(in);
			byte [] decoded = Base64.getMimeDecoder().decode(data);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
			return fac.generatePublic(keySpec);
		} catch (IOException e) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500, "fail to read RSA priv key");
		} catch (InvalidKeySpecException e) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500, "invalid key spec while generating RSa priv key");
		}
	}
	private byte[] readToBytes(InputStream in) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(1024*8);
		int b;
		while((b=in.read()) != -1) {
			bos.write(b);
		}
		return bos.toByteArray();
	}

	@Override
	public String encode(String plainSource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String decode(String encodedSource) {
		// TODO Auto-generated method stub
		return null;
	}

}
