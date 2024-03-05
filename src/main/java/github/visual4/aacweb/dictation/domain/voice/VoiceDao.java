package github.visual4.aacweb.dictation.domain.voice;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.Dao;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.voice.Voice.Column;

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
	public boolean existingVoice(String textHash) {
		int cnt = session.selectOne(Dao.mapper(this, "existingVoice"), textHash);
		return cnt > 0;
	}
	public List<String> selectHashes() {
		return session.selectList(Dao.mapper(this, "selectHashes"));
	}
	public List<Voice> searchBy(VoiceSearchParam param) {
		return session.selectList(Dao.mapper(this, "searchBy"), param);
	}
	public Integer countAll() {
		return session.selectOne(Dao.mapper(this, "countAll"));
	}
	public Voice findOneBy(Column column, Object value) {
		return Dao.selectOne(session, Dao.mapper(this, "findBy"), 
				TypeMap.with("column", column, "value", value));
	}
	public void updateOriginVoice(Voice altVoice) {
		Dao.updateOne(session, Dao.mapper(this, "updateOriginVoice"), altVoice);
		
	}
	public void updateAsConfirmed(Voice voice) {
		Dao.updateOne(session, Dao.mapper(this, "updateAsConfirmed"), voice);
	}
}
