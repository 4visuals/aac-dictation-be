package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;

public class MarkFactory {

	public Mark create(Difficulty df, Jamo pattern, CharSequence word) {
		Mark m = Mark.create(word);
		for(int k = 0; k < word.length(); k++) {
			char ch = word.charAt(k);
			if (pattern.matched(ch)) {
				m.mark(df, k);
			} else {
				m.reset();
			}
		}
		m.reset();
		return m;
	}
}
