package github.visual4.aacweb.dictation.korean.level;

import java.util.Set;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
/**
 * 1. 자음(ㅇㄱㄴㄷㄹㅁㅂㅅㅈ) + 모음(ㅏㅓㅗㅜㅡㅣ) + 받침 없음
 * @author chminseo
 *
 */
public class Level1 implements ILevel {

	Jamo pattern;
	
	Level1() {
		this.pattern = Jamo.pattern("ㅇㄱㄴㄷㄹㅁㅂㅅㅈ", "ㅏㅓㅗㅜㅡㅣ", "_");
	}
	
	@Override
	public Difficulty evaludate(CharSequence word) {
		/*
		 * 주어진 패턴의 자모만 등장해야 함
		 */
		for(int k = 0; k < word.length(); k++) {
			char ch = word.charAt(k);
			if (!this.pattern.matched(ch)) {
				return Difficulty.NONE;
			}
		}
		return Difficulty.L1;
	}

}
