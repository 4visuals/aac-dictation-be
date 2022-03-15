package github.visual4.aacweb.dictation.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

	@GetMapping("/")
	public String pageIndex() {
		return "index";
	}
	
	@GetMapping("/level")
	public String pageLevel() {
		return "index";
	}
}
