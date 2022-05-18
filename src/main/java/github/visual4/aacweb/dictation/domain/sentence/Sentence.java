package github.visual4.aacweb.dictation.domain.sentence;

import java.util.ArrayList;
import java.util.List;

import github.visual4.aacweb.dictation.tools.Origin;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Sentence {
	public enum Column {
		seq
	}
	public enum SentenceType {
		W, // 단어 학습(한 단어짜리 문장)
		S // 문장 학습(두 개 이상의 어절로 구성된 문장)
	}
	@EqualsAndHashCode.Include
	Integer seq;
	
	String sentence;
	Integer level;
	Origin origin;
	SentenceType type;
	Integer sectionRef;
	
	String scenePicture;
	
	List<EjElem> eojeols;
	
	Integer aacSentenceRef; // sentence_one의PK

	public Sentence() {}
	
	public Sentence(Integer seq, String sentence, Integer level, Origin origin, SentenceType type, String scenePicture, Integer sectionRef) {
		super();
		this.seq = seq;
		this.sentence = sentence;
		this.level = level;
		this.origin = origin;
		this.type = type;
		this.sectionRef = sectionRef;
		this.scenePicture = scenePicture;
		this.eojeols = new ArrayList<>();
	}
	
}
