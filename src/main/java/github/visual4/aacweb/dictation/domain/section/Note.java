package github.visual4.aacweb.dictation.domain.section;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author chminseo
 *
 */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Note {

	public enum NoteFormat {
		T, // text 단순 설명
		Y, // youtube link
		I, // image
	}
	@EqualsAndHashCode.Include
	Integer basketRef;
	
	@EqualsAndHashCode.Include
	Integer orderNum;
	
	String text;
	NoteFormat format;
}
