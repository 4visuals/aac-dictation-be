package github.visual4.aacweb.dictation.tools;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Eojeol {
	
	@EqualsAndHashCode.Include
	Integer seq;
	
	// table wr_eojeol에서 사용하는 필드들
	String text;
	Integer level;
	
	public Eojeol(Integer seq, String text, Integer level) {
		super();
		this.seq = seq;
		this.text = text;
		this.level = level;
	}

	

}
