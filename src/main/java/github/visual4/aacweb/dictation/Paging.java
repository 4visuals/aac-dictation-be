package github.visual4.aacweb.dictation;

import java.util.List;

import github.visual4.aacweb.dictation.domain.voice.VoiceSearchParam;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Paging<T> {

	List<T> elems;
	
	Integer total;

	VoiceSearchParam param;
	
	public Paging(List<T> elems, VoiceSearchParam param, Integer total) {
		super();
		this.elems = elems;
		this.param = param;
		this.total = total;
	}
	
	
	
}
