package github.visual4.aacweb.dictation.tools;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Chapter {

	@EqualsAndHashCode.Include
	Integer seq;
	String desc;
	Origin origin;
	List<Section> sections;
	public Chapter(Integer seq, String desc, Origin origin) {
		super();
		this.seq = seq;
		this.desc = desc;
		this.origin = origin;
		this.sections = new ArrayList<>();
	}
	
	public void addSection(Section section) {
		sections.add(section);
	}
	
	
}
