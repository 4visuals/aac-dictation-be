package github.visual4.aacweb.dictation.service.analysis;

import org.springframework.stereotype.Service;

import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.section.Section;
import github.visual4.aacweb.dictation.korean.Mark;
import github.visual4.aacweb.dictation.korean.level.LevelContext;

@Service
public class AnalysisService {

	
	public Mark parseDifficulties(String text) {
		LevelContext ctx = new LevelContext();
		Mark mark = ctx.parseDifficulties(text.trim());
		return mark;
	}

	public TypeMap parseDifficulties(Section section) {
		LevelContext ctx = new LevelContext();
		TypeMap dfMap = new TypeMap();
		section.getSentences().forEach(sen -> {
			Mark mark = ctx.parseDifficulties(sen.getSentence().trim());
			dfMap.put("" +sen.getSeq(), mark.toMap());
		});
		return dfMap;
		
	}
}
