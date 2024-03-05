package github.visual4.aacweb.dictation.service.storage;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import github.visual4.aacweb.dictation.Util;
import github.visual4.aacweb.dictation.service.storage.local.LocalStorage;

@Controller
@RequestMapping("/lf")
public class FileStorageController {
	
	final LocalStorage local;
	
	public FileStorageController(LocalStorage local) {
		this.local = local;
	}

	@GetMapping(value="/{category}/{fileName}", produces = "audio/mpeg")
	public void sendVoiceFile(
			@PathVariable String category,
			@PathVariable String fileName,
			HttpServletResponse res) {
		
		byte [] data = local.open(category, fileName);
		System.out.println("[audio] " + data.length);
		res.setContentType("audio/mpeg");
		res.setContentLength(data.length);
		
		Util.write(data, res);
	}
}
