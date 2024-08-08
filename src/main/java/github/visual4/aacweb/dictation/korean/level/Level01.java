package github.visual4.aacweb.dictation.korean.level;

import java.util.Set;


import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 1. 자음(ㅇㄱㄴㄷㄹㅁㅂㅅㅈ) + 모음(ㅏㅓㅗㅜㅡㅣ) + 받침 없음
 * @author chminseo
 *
 */
public class Level01 implements ILevel {

	final Jamo pattern = Jamo.pattern("ㅇㄱㄴㄷㄹㅁㅂㅅㅈ", "ㅏㅓㅗㅜㅡㅣ", "*");
	final LevelContext ctx;
	
	public Level01(LevelContext ctx ) {
		this.ctx = ctx;
	}

	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		for(int k = 0 ; k < word.length(); k++) {
			if(pattern.matched(word.charAt(k))) {
				mk.addRange(Difficulty.L01, k, 0, k + 1, -1);
			}
		}
		return mk;
//		return ctx.setMark(Difficulty.L01, pattern, word);
	}

}
