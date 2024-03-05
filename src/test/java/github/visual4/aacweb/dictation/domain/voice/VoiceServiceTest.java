package github.visual4.aacweb.dictation.domain.voice;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import github.visual4.aacweb.dictation.BaseDao;
import github.visual4.aacweb.dictation.Paging;
import github.visual4.aacweb.dictation.service.storage.StorageService;
import github.visual4.aacweb.dictation.service.tts.ITtsHandler;

@Import(VoiceServiceImport.class)
class VoiceServiceTest extends BaseDao {

	@Autowired
	VoiceService voiceService;
	
	@MockBean ITtsHandler ttsHandler;
	@MockBean StorageService storage;
	
	@Test
	void test() {
		VoiceSearchParam param = new VoiceSearchParam();
		param.ascending = true;
		param.offset = 0;
		param.size = 10;
		Paging<Voice> pg = voiceService.searchBy(param);
		assertEquals(10, pg.getElems().size());
	}

}
