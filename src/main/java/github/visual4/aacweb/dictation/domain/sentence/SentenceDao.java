package github.visual4.aacweb.dictation.domain.sentence;

import java.sql.ResultSet;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.Dao;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.sentence.Sentence.SentenceType;
import github.visual4.aacweb.dictation.tools.Origin;
import github.visual4.aacweb.dictation.tools.Rset;

@Repository
public class SentenceDao {

	@Autowired
	SqlSession session;
	
	public List<Sentence> findBy(Sentence.Column col, Object value) {
		return session.selectList(Dao.mapper(this, "findBy"),
				TypeMap.with("col", col.name(), "val", value));
	}
	public static Sentence rsToSentence(Rset rs) {
		Integer seq = rs.Int("seq");
		String sentence = rs.str("sentence");
		Integer level = rs.Int("level");
		Origin origin = Origin.valueOf(rs.str("origin"));
		SentenceType type = SentenceType.valueOf(rs.str("type"));
		String scenePicture = rs.str("scene_pic");
		Integer sectionRef = rs.Int("cate");
		
		Sentence sen = new Sentence(seq, sentence, level, origin, type, scenePicture, sectionRef);
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
