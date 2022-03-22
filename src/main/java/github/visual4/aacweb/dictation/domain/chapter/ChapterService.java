package github.visual4.aacweb.dictation.domain.chapter;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.visual4.aacweb.dictation.tools.Origin;

@Service
@Transactional
public class ChapterService {

	final ChapterDao chapterDao;
	
	public ChapterService(ChapterDao chapterDao) {
		this.chapterDao = chapterDao;
	}
	public List<Chapter> findChapters(Origin origin) {
		return chapterDao.findChapters(origin);
	}
	
}
