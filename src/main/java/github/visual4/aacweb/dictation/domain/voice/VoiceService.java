package github.visual4.aacweb.dictation.domain.voice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.visual4.aacweb.dictation.Util;
import github.visual4.aacweb.dictation.service.storage.StorageService;
import github.visual4.aacweb.dictation.service.tts.ITts;
import github.visual4.aacweb.dictation.service.tts.ITtsHandler;

@Service
public class VoiceService {

	final ITtsHandler ttsHandler;
	final VoiceDao voiceDao;
	final StorageService storageService;

	public VoiceService(ITtsHandler ttsHandler, VoiceDao voiceDao, StorageService storageService) {
		this.ttsHandler = ttsHandler;
		this.voiceDao = voiceDao;
		this.storageService = storageService;
	}
	
	@Transactional
	public void download(String text) {
		text = text.trim();
		ITts tts = ttsHandler.speak(text);
		String textHash = Util.Hash.md5(text).toLowerCase();
		StreamSourceFile meta = new StreamSourceFile(textHash + ".mp3", tts.getStream());
//		meta.getBytes(); // prefetch
		Voice voice = meta.toVoice(textHash, text);
		voiceDao.insertVoice(voice);			storageService.store(meta);
	}

	public Voice findVoice(String textHash) {
		return null;
	}
	
	
	
}
