package github.visual4.aacweb.dictation.domain.chapter;

import static github.visual4.aacweb.dictation.Dao.*;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.tools.Origin;
import github.visual4.aacweb.dictation.tools.Rset;

@Repository
public class ChapterDao {

	final public static String [] FULL_COLS = "seq as chapSeq,desc as chapDesc,origin".split(",");

	final SqlSession session;
	
	public ChapterDao(SqlSession session) {
		this.session = session;
	}
	
	public List<Chapter> findChapters(Origin origin) {
		return session.selectList(
				mapper(this, "findChapters"), 
				origin != null ? origin.name() : null);
	}

	@Deprecated
	public static Chapter rsToChapter(Rset rs) {
		return new Chapter(
				rs.Int("chapSeq"),
				rs.str("chapDesc"),
				Origin.valueOf(rs.str("origin")));
	}
}
