package github.visual4.aacweb.dictation.domain.sentence;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SentenceService {

	final SentenceDao sentenceDao;

	public SentenceService(SentenceDao sentenceDao) {
		this.sentenceDao = sentenceDao;
	}
	
	public Sentence findBySeq(Integer sentenceSeq) {
		List<Sentence> sentences = sentenceDao.findBy(Sentence.Column.seq, sentenceSeq);
		return sentences.size() == 0 ? null : sentences.get(0);
	}
	
	
}
