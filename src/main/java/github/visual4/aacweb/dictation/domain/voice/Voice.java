package github.visual4.aacweb.dictation.domain.voice;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Voice {

	public enum Column {
		vseq,
		text_hash,
		voice_origin,
		file_path
	}
	
	@EqualsAndHashCode.Include
	Integer seq;
	
	String originText;
	
	String textHash;
	
	String origin;
	
	String filePath;
	
	Integer fileSize;
	
}
