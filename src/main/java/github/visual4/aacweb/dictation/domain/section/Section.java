package github.visual4.aacweb.dictation.domain.section;

import java.util.List;

import github.visual4.aacweb.dictation.domain.sentence.Sentence;
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

	@EqualsAndHashCode.Include
	Integer seq;
	
	String description;
	Integer level;
	
	Integer basketRef;
	
	List<Note> notes;
	
	List<Sentence> sentences;
}
