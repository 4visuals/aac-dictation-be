package github.visual4.aacweb.dictation.domain.section;

import java.util.List;

import github.visual4.aacweb.dictation.domain.sentence.Sentence;
import github.visual4.aacweb.dictation.tools.Origin;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 문장을 포함하는 단위(난이도 있음)
 * @author chminseo
 *
 */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Section {

	public enum Column {
		seq
	};

	@EqualsAndHashCode.Include
	Integer seq;
	
	String description;
	Integer level;
	
	Integer basketRef;
	
	List<Note> notes;
	
	List<Sentence> sentences;
	
	Integer chapterRef;

	public Origin getOrigin() {
		// seq: 1 ~ 60까지는 단계별 학습, 61부터는 교과서 학습
		if (seq <= 60) {
			return Origin.L;
		} else {			
			return Origin.B;
		}
	}
}
