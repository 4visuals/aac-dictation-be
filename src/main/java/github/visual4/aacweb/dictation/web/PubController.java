package github.visual4.aacweb.dictation.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import github.visual4.aacweb.dictation.Res;
import github.visual4.aacweb.dictation.domain.tool.PubService;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class PubController {

	@Autowired
	PubService pubService;
	
	@PostMapping("/publish")
	public Object updateDb(@RequestBody String query) {
//		log.warn("[query] {}", query);
//		System.out.println("[query한글]" + query);
		pubService.publishDb(query);
		return Res.success(true);
	}
}
