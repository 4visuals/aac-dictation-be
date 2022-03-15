package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
/**
 * 2. 자음(ㅇㄱㄴㄷㄹㅁㅂㅅㅈ) + 모음(ㅑㅕㅛㅠ) + 받침 없음
 * @author chminseo
 *
 */
public class Level2 implements ILevel {

	final Jamo pattern;
	
	Level2() {
		this.pattern = Jamo.pattern("ㅇㄱㄴㄷㄹㅁㅂㅅㅈ", "ㅑㅕㅛㅠ", "_");
	}
	
	@Override
	public Difficulty evaludate(CharSequence word) {
		/*
		 * 한글자라도 일치하면 통과함
		 */
		Difficulty df = Difficulty.NONE;
		for(int k = 0; k < word.length(); k++) {
			Jamo ch = Jamo.decompose(word.charAt(k));
			// char ch = word.charAt(k);
			if (ch.hasJongsung()) {
				return Difficulty.NONE;
			}
			if (this.pattern.matched(ch)) {
				df = Difficulty.L2;
			}
		}
		return df;
	}

}
