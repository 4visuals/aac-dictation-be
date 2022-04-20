package github.visual4.aacweb.dictation.domain.exam;

import java.util.List;
import java.util.function.Consumer;

import github.visual4.aacweb.dictation.domain.sentence.Sentence.SentenceType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EojeolPaper {

	String license;
	List<EojeolAnswer> submissions;
	/**
	 * W - [낱말쓰기]에서 입력됨
	 * S - [문장쓰기]에서 입력됨
	 */
	SentenceType type;
	public void forEachAnswer(Consumer<EojeolAnswer> fn) {
		submissions.forEach(fn);
	}
}
