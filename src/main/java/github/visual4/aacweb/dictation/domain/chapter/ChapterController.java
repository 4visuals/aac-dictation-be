package github.visual4.aacweb.dictation.domain.chapter;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.visual4.aacweb.dictation.Res;
import github.visual4.aacweb.dictation.domain.section.SectionService;
import github.visual4.aacweb.dictation.tools.Chapter;
import github.visual4.aacweb.dictation.tools.Origin;

@RestController
@RequestMapping("/api/chapters")
public class ChapterController {

	@Autowired
	SectionService sectionService;
	
	@GetMapping("/origin/{origin}")
	public Object listChapters(@PathVariable Origin origin) {
		Collection<Chapter> chapters = sectionService.findChapter(origin);
		return Res.success("chapters", chapters);
	}
}
