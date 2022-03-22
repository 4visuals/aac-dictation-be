package github.visual4.aacweb.dictation.domain.section;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.visual4.aacweb.dictation.Res;
import github.visual4.aacweb.dictation.domain.chapter.Chapter;
import github.visual4.aacweb.dictation.domain.sentence.Sentence;
import github.visual4.aacweb.dictation.domain.sentence.Sentence.SentenceType;
import github.visual4.aacweb.dictation.tools.Origin;

@RestController
@RequestMapping("/api")
public class SectionController {

	
	@Autowired
	SectionService sectionService;
	
	public SectionController() {
		System.out.println("is it done?");
	}
	
	@GetMapping("/dictations/level")
	public Object listSections() {
//		List<Section> sections = sectionService.findByLevel();
		Collection<Chapter> chapters = sectionService.findChapter(Origin.L);
		return Res.success("chapters", chapters);
		
	}
	/**
	 * 주어진 section의 문장 또는 어휘들
	 * @param sectionSeq
	 * @param type - 'S'문장, 'W'어휘
	 * @return
	 */
	@GetMapping("/section/{sectionSeq}/{type}")
	public Object sentencesOfSections(
			@PathVariable Integer sectionSeq,
			@PathVariable SentenceType type) {
		Collection<Sentence> sentences = sectionService.findSentencesBySection(sectionSeq, type);
		return Res.success("sentences", sentences);
	}
	
}
