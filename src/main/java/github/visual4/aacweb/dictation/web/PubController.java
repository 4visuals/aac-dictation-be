package github.visual4.aacweb.dictation.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import github.visual4.aacweb.dictation.Res;
import github.visual4.aacweb.dictation.domain.tool.PubService;

@RestController
public class PubController {

	final PubService pubService;

    PubController(PubService pubService) {
        this.pubService = pubService;
    }
	
	@PostMapping("/publish")
	public Object updateDb(@RequestBody String query) {
		pubService.publishDb(query);
		return Res.success(true);
	}
}
