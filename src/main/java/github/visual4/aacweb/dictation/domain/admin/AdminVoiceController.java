package github.visual4.aacweb.dictation.domain.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.visual4.aacweb.dictation.Paging;
import github.visual4.aacweb.dictation.Res;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.voice.StorageTarget;
import github.visual4.aacweb.dictation.domain.voice.Voice;
import github.visual4.aacweb.dictation.domain.voice.VoiceSearchParam;
import github.visual4.aacweb.dictation.domain.voice.VoiceService;

@RestController
@RequestMapping("/api/admin")
public class AdminVoiceController {

	final VoiceService voiceService;

	public AdminVoiceController(VoiceService voiceService) {
		this.voiceService = voiceService;
	}

	@GetMapping("/voices")
	public Object searchVoices() {
		VoiceSearchParam param = new VoiceSearchParam();
		param.setOffset(0);
		Paging<Voice> paging = voiceService.searchBy(param);
		return Res.success("paging", paging);
	}
	
	@PostMapping("/voice")
	public Object fetchLocalVoice(@RequestBody TypeMap param) {
		String text = param.getStr("alternativeText");
		Voice voice = voiceService.download(text, StorageTarget.local);
		return Res.success("voice", voice);
	}
	/**
	 * 음성 파일 교체. originVoiceSeq 를 altVoiceSeq로 교체함 
	 * 
	 * @param originVoiceSeq - 교체될 음성 파일(PK to dt_voices)
	 * @param altVoiceSeq - 새로운 음성 파일(PK to dt_voices)
	 * @return
	 */
	@PutMapping("/voice/{originVoiceSeq}/alternative/{altVoiceSeq}")
	public Object replaceVoiceFile(
			@PathVariable Integer originVoiceSeq,
			@PathVariable Integer altVoiceSeq
			) {
		Voice origin = voiceService.replaceVoiceFile(originVoiceSeq, altVoiceSeq);
		return Res.success("origin", origin);
	}
	/**
	 * 음성 파일 확인했음
	 */
	@PutMapping("/voice/{voiceSeq}/confirm")
	public Object confirmVoice(@PathVariable Integer voiceSeq) {
		Voice voice = voiceService.markAsConfirmed(voiceSeq);
		return Res.success("voice", voice);
	}
	
}
