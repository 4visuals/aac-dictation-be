package github.visual4.aacweb.dictation.domain.section;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import github.visual4.aacweb.dictation.config.AacDaoConfig;
import github.visual4.aacweb.dictation.tools.Origin;

@MybatisTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({AacDaoConfig.class, SectionDao.class})
class SectionDaoTest {

	@Autowired
	SectionDao sectionDao;
	
	@Test
	void test() throws SQLException {
		Section section = sectionDao.find(1);
		System.out.println(section);
	}

	@Test
	public void test_byOrigin() {
		List<Section> sections = sectionDao.findByOrigin(Origin.L);
		assertNotNull(sections);
		assertTrue(sections.size() > 0);
	}
}
