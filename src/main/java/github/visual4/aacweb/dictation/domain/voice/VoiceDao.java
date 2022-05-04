package github.visual4.aacweb.dictation.domain.voice;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.Dao;

@Repository
public class VoiceDao {

	final SqlSession session;
	
	public VoiceDao(SqlSession session) {
		super();
		this.session = session;
	}
	public void insertVoice(Voice voice) {
		session.insert(Dao.mapper(this, "insertVoice"), voice);
	}
}
