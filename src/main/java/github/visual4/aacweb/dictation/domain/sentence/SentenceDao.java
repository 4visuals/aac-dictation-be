package github.visual4.aacweb.dictation.domain.sentence;

import java.sql.ResultSet;

import github.visual4.aacweb.dictation.tools.EjElem;
import github.visual4.aacweb.dictation.tools.Origin;
import github.visual4.aacweb.dictation.tools.Rset;
import github.visual4.aacweb.dictation.tools.Sentence;
import github.visual4.aacweb.dictation.tools.Sentence.SentenceType;

public class SentenceDao {

	
	public static Sentence rsToSentence(Rset rs) {
		Integer seq = rs.Int("seq");
		String sentence = rs.str("sentence");
		Integer level = rs.Int("level");
		Origin origin = Origin.valueOf(rs.str("origin"));
		SentenceType type = SentenceType.valueOf(rs.str("type"));
		Integer sectionRef = rs.Int("cate");
		
		Sentence sen = new Sentence(seq, sentence, level, origin, type, sectionRef);
		return sen;
	}

	public static EjElem rsToEojel(Rset r) {
		Integer senRef = r.Int("wr_sen");
		Integer eojeolRef = r.Int("wr_eojeol");
		String pumsa = r.str("pumsa");
		String picUrl = r.str("pic");
		Integer order = r.Int("ordernum");
		String text = r.str("text");
		EjElem elem = new EjElem(senRef, order, eojeolRef, 
				text,
				pumsa,
				picUrl);
		return elem;
	}

}
