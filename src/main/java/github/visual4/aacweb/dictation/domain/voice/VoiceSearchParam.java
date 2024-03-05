package github.visual4.aacweb.dictation.domain.voice;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoiceSearchParam {
	
	Voice.Column where;
	Object keyword;
	
	Boolean ascending;
	
	Integer offset;
	Integer size;
}
