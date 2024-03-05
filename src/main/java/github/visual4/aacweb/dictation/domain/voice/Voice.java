package github.visual4.aacweb.dictation.domain.voice;

import java.time.Instant;

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
	
	StorageTarget origin;
	
	String filePath;
	
	Integer fileSize;
	
	Integer voiceVersion;
	
	Instant confirmDate;
	/**
	 * 현재 음성파일을 대체하는 음성파일의 PK 
	 */
	Integer altVoiceRef;
	
}
