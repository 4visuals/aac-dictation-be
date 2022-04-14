package github.visual4.aacweb.dictation.domain.chapter;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import github.visual4.aacweb.dictation.BaseDao;
import github.visual4.aacweb.dictation.domain.section.Section;

@Import(ChapterDao.class)
class ChapterDaoTest extends BaseDao {

	@Autowired
	ChapterDao chapterDao;
	
	@Test
	void test() {
		List<Chapter> chapters = chapterDao.findChapters(null);
		assertNotNull(chapters);
		Section first = chapters.get(0).getSections().get(0);
		System.out.println(first.getSentences());
//		System.out.println(chapters.get(0).getSections().get(0));
	}

}
