package github.visual4.aacweb.dictation.tools;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class EjElem {

	@EqualsAndHashCode.Include
	Integer sentenceRef;
	@EqualsAndHashCode.Include
	Integer order;
	
	Integer eojeolRef;
	String text;
	String pumsa;
	String picturePath;
	public EjElem(
			Integer sentenceRef,
			Integer order,
			Integer eojeolRef,
			String text,
			String pumsa,
			String picturePath) {
		super();
		this.sentenceRef = sentenceRef;
		this.order = order;
		this.eojeolRef = eojeolRef;
		this.text = text;
		this.pumsa = pumsa;
		this.picturePath = picturePath;
	}

	
}
