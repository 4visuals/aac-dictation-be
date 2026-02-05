package github.visual4.aacweb.dictation.domain.voice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class VoiceController {

	final VoiceService voiceService;

	public VoiceController(VoiceService voiceService) {
		this.voiceService = voiceService;
	}
}
