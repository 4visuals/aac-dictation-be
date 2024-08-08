package github.visual4.aacweb.dictation.service.analysis;

import java.util.List;

public class SentenceTag {

	public String sen;
	public List<Tagging> tags;
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SentenceTag [sen=").append(sen).append(", tags=").append(tags).append("]");
		return builder.toString();
	}
	
	
}
