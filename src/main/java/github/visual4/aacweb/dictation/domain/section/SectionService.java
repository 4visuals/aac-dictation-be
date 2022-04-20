package github.visual4.aacweb.dictation.domain.section;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.visual4.aacweb.dictation.domain.chapter.Chapter;
import github.visual4.aacweb.dictation.domain.sentence.Sentence;
import github.visual4.aacweb.dictation.domain.sentence.Sentence.SentenceType;
import github.visual4.aacweb.dictation.tools.Origin;

@Service
@Transactional
public class SectionService {

	@Autowired
	SectionDao sectionDao;
	
	public List<Section> findByLevel() {
		return sectionDao.findByOrigin(Origin.L);
	}
	public Collection<Chapter> findChapter(Origin origin) {
		return sectionDao.findChapters(origin);
	}
	
	public Collection<Sentence> findSentencesBySection(Integer sectionSeq, SentenceType type) {
		return sectionDao.findSentencesBySection(sectionSeq, type);
	}
	public Section findBy(Section.Column column, Object value) {
		return sectionDao.findBy(column, value);
	}
}
