package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 40. 받침 ㄳ ㄵ ㅄ
 * 
 * @author chminseo
 */
public class Level40 implements ILevel {
	final Jamo pattern = Jamo.pattern("*", "*", "ㄳㄵㅄ");
	final LevelContext ctx;
	
	Level40(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		for(int k = 0 ; k < word.length(); k++) {
			if(pattern.matched(word.charAt(k))) {
				// 받침만 표시해야 함
				mk.addRange(Difficulty.L40, k, 2, k + 1, 0);
			}
		}
		return mk;
	}

}
