package github.visual4.aacweb.dictation.domain.chapter;

import github.visual4.aacweb.dictation.tools.Chapter;
import github.visual4.aacweb.dictation.tools.Origin;
import github.visual4.aacweb.dictation.tools.Rset;

public class ChapterDao {

	final public static String [] FULL_COLS = "seq as chapSeq,desc as chapDesc,origin".split(",");
	
	public static Chapter rsToChapter(Rset rs) {
		return new Chapter(
				rs.Int("chapSeq"),
				rs.str("chapDesc"),
				Origin.valueOf(rs.str("origin")));
	}
}
