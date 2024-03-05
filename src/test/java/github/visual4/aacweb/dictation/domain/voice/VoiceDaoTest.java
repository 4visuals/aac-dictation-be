package github.visual4.aacweb.dictation.domain.voice;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import github.visual4.aacweb.dictation.BaseDao;

@Import(VoiceDao.class)
class VoiceDaoTest extends BaseDao {

	@Autowired
	VoiceDao voiceDao;
	
	@Test
	void testInsert() {
		Voice v = new Voice();
		v.setFilePath("dummy.mp3");
		v.setFileSize(100);
		v.setOrigin(StorageTarget.local);
		v.setOriginText("ok");
		v.setTextHash("1ccc1");
		v.setVoiceVersion(2024);
		voiceDao.insertVoice(v);
		assertNotNull(v.getSeq());
	}

}
