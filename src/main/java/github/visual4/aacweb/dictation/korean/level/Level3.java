package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
/**
 * 3. 자음(ㅊㅋㅌㅍㅎ) + 모음 + 받침 없음
 * 
 * @author chminseo
 */
public class Level3 implements ILevel {
	final Jamo pattern;
	
	Level3() {
		this.pattern = Jamo.pattern("ㅊㅋㅌㅍㅎ", "*", "_");
	}
	
	@Override
	public Difficulty evaludate(CharSequence word) {
		/*
		 * 한글자라도 일치하면 통과함
		 */
		Difficulty df = Difficulty.NONE;
		for(int k = 0; k < word.length(); k++) {
			Jamo ch = Jamo.decompose(word.charAt(k));
			if (ch.hasJongsung()) {
				return Difficulty.NONE;
			}
			if (this.pattern.matched(ch)) {
				df = Difficulty.L3;
			}
		}
		return df;
	}

}
