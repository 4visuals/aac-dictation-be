package github.visual4.aacweb.dictation.tools;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 문장을 포함하는 단위(난이도 있음)
 * @author chminseo
 *
 */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Section {

	@EqualsAndHashCode.Include
	Integer seq;
	
	String description;
	Integer level;
	
	Integer basketRef;
}
