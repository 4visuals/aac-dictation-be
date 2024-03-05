package github.visual4.aacweb.dictation.service.storage.ncp;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import github.visual4.aacweb.dictation.BaseDao;

@Import(NcpStorage.class)
class NcpStorageTest extends BaseDao {

	@Autowired
	NcpStorage storage;
	
	@Test
	@Disabled
	void test_fetch() {
		// 테스트 하나 둘 셋 넷
		ByteArrayOutputStream bos = new ByteArrayOutputStream(9837);
		storage.fetch((none) -> "voices3/d6bc1ae2f56ff0ca606a9f33b43139c0.mp3", bos, true);
		byte [] data = bos.toByteArray();
		assertEquals(9837, data.length);
	}

}
