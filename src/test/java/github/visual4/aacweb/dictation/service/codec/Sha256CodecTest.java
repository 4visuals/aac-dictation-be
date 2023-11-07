package github.visual4.aacweb.dictation.service.codec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.Test;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;

class Sha256CodecTest {

	@Test
	void test_encode_public() {
		Sha256Codec codec = new Sha256Codec();
		assertThat(codec.encode("public")).isEqualTo("EFA1F375D76194FA51A3556A97E641E61685F914D446979DA50A551A4333FFD7");
		assertThat(codec.encode("static")).isEqualTo("2053DBBF6EC7135C4E994D3464C478DB6F48D3CA21052C8F44915EDC96E02C39");
		assertThat(codec.encode("안녕")).isEqualTo("E8F817F346D1D411CC59D5BDDA64FAB3763890E1F0F8F4C15805CF78874D68BF");
		assertThat(codec.encode("나는 배고프다. 나는 배고프다! 나는 배고프다아!!!")).isEqualTo("FDF5C04F577689E57FC011ED067B8DBC1F50195AF54AA9467DE3E2DD737A329D");
	}
	@Test
	void test() {
		long initTime = 0;
		long procTime = 0;
		MessageDigest sha256;
		String sample = "a9iasld82lasodgkdk w983isja3 2dfasd3ks8gidiasf33";
		for(int i = 1 ; i < 10000000; i++) {
			long t = System.nanoTime();
			sha256 = init();
			initTime =+ (System.nanoTime() - t);
			
			int offset = (int) (Math.random()*(sample.length() - 5));
			String token = sample.substring(offset);
			byte [] in = token.getBytes();
			t = System.nanoTime();
			sha256.update(in);
			sha256.digest(in);
			procTime += (System.nanoTime() - t);
		}
		System.out.printf("init: %d, proc: %d\n", initTime, procTime);
	}
	
	private MessageDigest init() {
		try {
			return MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500, "fail to create SHA-256 instance.");
		}
	}

}
