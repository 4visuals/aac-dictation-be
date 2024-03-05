package github.visual4.aacweb.dictation.domain.voice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.Paging;
import github.visual4.aacweb.dictation.Util;
import github.visual4.aacweb.dictation.service.storage.StorageService;
import github.visual4.aacweb.dictation.service.storage.local.LocalStorage;
import github.visual4.aacweb.dictation.service.storage.ncp.NcpStorage;
import github.visual4.aacweb.dictation.service.tts.ITts;
import github.visual4.aacweb.dictation.service.tts.ITtsHandler;

@Service
@Transactional
public class VoiceService {

	final ITtsHandler ttsHandler;
	final VoiceDao voiceDao;
	final StorageService storageService;

	public VoiceService(ITtsHandler ttsHandler, VoiceDao voiceDao, StorageService storageService) {
		this.ttsHandler = ttsHandler;
		this.voiceDao = voiceDao;
		this.storageService = storageService;
	}
	/**
	 * 주어진 텍스트에 해당하는 음성 파일을 내려받아서 ncp에 저장함
	 * @param text
	 */
	public void download(String text) {
		download(text, StorageTarget.ncp);
	}
	/**
	 * 주어진 텍스트에 해당하는 음성 파일을 내려받음
	 * @param text
	 * @return
	 */
	public Voice download(String text, StorageTarget origin) {
		text = text.trim();
		String textHash = Util.Hash.md5(text).toLowerCase();
		Voice voice = voiceDao.findOneBy(Voice.Column.text_hash, textHash);
		if(voice != null) {
			return voice;
		}
		ITts tts = ttsHandler.speak(text);
		StreamSourceFile meta = new StreamSourceFile(textHash + ".mp3", tts.getStream());
		voice = meta.toVoice(textHash, text, origin);
		voiceDao.insertVoice(voice);
		storageService.store(meta, origin);
		return voice;
	}
	
	public Voice findVoice(String textHash) {
		return null;
	}
	
	public Paging<Voice> searchBy(VoiceSearchParam param) {
		Integer total = voiceDao.countAll();
		param.setSize(total);
		List<Voice> voices = voiceDao.searchBy(param);
		return new Paging<>(voices, param, total);
	}
	/**
	 * 음성 파일 교체. originVoiceSeq 를 altVoiceSeq로 교체함
	 * 
	 * @param originVoiceSeq - 교체될 음성 파일(PK to dt_voices)
	 * @param altVoiceSeq - 새로운 음성 파일(PK to dt_voices)
	 * @return 
	 */
	public Voice replaceVoiceFile(Integer originVoiceSeq, Integer altVoiceSeq) {
		Voice origin = voiceDao.findOneBy(Voice.Column.vseq, originVoiceSeq);
		checkVoice(origin, StorageTarget.ncp);
		Voice alt = voiceDao.findOneBy(Voice.Column.vseq, altVoiceSeq);
		checkVoice(alt, StorageTarget.local);
		
		LocalStorage local = storageService.getLocalStorage();
		NcpStorage ncp = storageService.getNcpStorage();
		
		try {
			Instant now = Instant.now();
			origin.setConfirmDate(now);
			origin.setAltVoiceRef(alt.getSeq());
			voiceDao.updateOriginVoice(origin);
			
			alt.setConfirmDate(now);
			voiceDao.updateAsConfirmed(alt);
		
			// 원본을 로컬에 백업
			File backupFile = local.touchFile(origin.getFilePath());
			ncp.fetch(
					none -> "voices3/" + origin.getFilePath(),
					new FileOutputStream(backupFile),
					true);
			// altFile을 업로드함
			File altFile = local.getVoiceFile(alt.getFilePath());
			StreamSourceFile upfile = new StreamSourceFile(
					origin.getFilePath(), // origin의 경로를 사용해서 덮어씀
					new FileInputStream(altFile));
			ncp.upload(upfile, f -> "voices3/" + f.getFileName());
			return origin;
		} catch ( IOException e) {
			e.printStackTrace();
			throw new AppException(ErrorCode.SERVER_ERROR, 500, "cause: " + e.getMessage());
		}
		
	}
	private void checkVoice(Voice voice, StorageTarget expected) {
		if(voice.getOrigin() != expected) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500, 
					"voice should be " + expected + ", but " + voice.getOrigin());
		}
		
	}
	public Voice markAsConfirmed(Integer voiceSeq) {
		Voice voice = voiceDao.findOneBy(Voice.Column.vseq, voiceSeq);
		checkVoice(voice, StorageTarget.ncp);
		
		voice.setConfirmDate(Instant.now());
		voiceDao.updateAsConfirmed(voice);
		return voice;
	}
	
}
