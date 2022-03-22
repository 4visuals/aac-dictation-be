package github.visual4.aacweb.dictation.domain.section;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.Dao;
import github.visual4.aacweb.dictation.Query;
import github.visual4.aacweb.dictation.domain.chapter.Chapter;
import github.visual4.aacweb.dictation.domain.chapter.ChapterDao;
import github.visual4.aacweb.dictation.domain.sentence.EjElem;
import github.visual4.aacweb.dictation.domain.sentence.Sentence;
import github.visual4.aacweb.dictation.domain.sentence.SentenceDao;
import github.visual4.aacweb.dictation.domain.sentence.Sentence.SentenceType;
import github.visual4.aacweb.dictation.tools.Origin;
import github.visual4.aacweb.dictation.tools.Rset;

@Repository
public class SectionDao {

	@Autowired
	JdbcTemplate jdbc;
	
	final SqlSession session;
	
	public SectionDao(SqlSession session) {
		this.session = session;
	}

	final private static String [] FULL_COLUMN = "seq as secSeq,desc,level".split(",");
	
	public Section find(Integer sectionSeq) {
		return session.selectOne(Dao.mapper(this, "find"), sectionSeq);
	}
	public List<Section> findByOrigin(Origin origin) {
		return session.selectList(Dao.mapper(this, "findByOrigin"), origin.name());
	}

	/**
	 * 단계별 퀴즈
	 * @return
	 */
	public List<Section> findSections(Origin origin) {
		String q = "SELECT @chapter, @section"
				+ "FROM wr_chapter ctr "
				+ "JOIN wr_section sec "
				+ "  ON ctr.seq  = sec.chapter "
				+ "JOIN wr_sentence ws  "
				+ "  ON sec.seq  = ws.cate "
				+ "WHERE ctr.origin = ?";
		String query = new Query(q)
				.bind("@chapter", "ctr", ChapterDao.FULL_COLS)
				.bind("@section", "sec", FULL_COLUMN)
				.get();
		
		return jdbc.query(query, (rs, index ) -> {
			return rsToSection(rs, index);
		});
	}
	
	public Collection<Chapter> findChapters(Origin origin) {
		String q = "SELECT @chapter, @section "
				+ "FROM wr_chapter ctr "
				+ "JOIN wr_section sec "
				+ "  ON ctr.seq  = sec.chapter "
				+ "WHERE ctr.origin = ?";
		String query = new Query(q)
				.bind("@chapter", "ctr", ChapterDao.FULL_COLS)
				.bind("@section", "sec", FULL_COLUMN)
				.get();
		Map<Integer, Chapter> chapters = new TreeMap<>();
		
		jdbc.query(query, (rs, index ) -> {
			Rset r = new Rset(rs);
			Chapter c = chapters.get(r.Int("chapSeq"));
			if (c == null) {
				c = ChapterDao.rsToChapter(r);
				chapters.put(c.getSeq(), c);
			}
			Section section = rsToSection(rs, index);
			c.addSection(section);
			return null; 
		}, origin.name());
		return chapters.values();
	}

	public Section rsToSection(ResultSet rs, int index) {
		Rset row = new Rset(rs);
		Section section= new Section();
		section.setSeq(row.Int("secSeq"));
		section.setDescription(row.str("desc"));
		section.setLevel(row.Int("level"));
		return section;
	}

	public Collection<Sentence> findSentencesBySection(Integer sectionSeq, SentenceType type) {
		String q = "SELECT "
				+ "sen.*, "
				+ "wrp.*, "
				+ "ej.`text`  "
				+ "FROM wr_sentence sen "
				+ "JOIN wr_mapping wrp ON sen.seq = wrp.wr_sen  "
				+ "JOIN wr_eojeol ej ON wrp.wr_eojeol = ej.seq "
				+ "WHERE sen.cate = ? and sen.`type` = ?";
		
		Map<Integer, Sentence> senMap = new TreeMap<>();
		jdbc.query(q,(rs, index) -> {
			Rset r = new Rset(rs);
			Sentence sen = senMap.get(r.Int("seq"));
			if (sen == null) {
				sen = SentenceDao.rsToSentence(r);
				senMap.put(sen.getSeq(), sen);
			}
			EjElem eojel = SentenceDao.rsToEojel(r);
			sen.getEojeols().add(eojel);
			return null;
		}, sectionSeq, type.name());
		
		return senMap.values();
		
	}
	
	
}
