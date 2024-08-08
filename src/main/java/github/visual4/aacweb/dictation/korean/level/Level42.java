package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 42. 받침 ㄼ ㄽ ㄾ
 * 
 * @author chminseo
 */
public class Level42 implements ILevel {
	final Jamo pattern = Jamo.pattern("*", "*", "ㄼㄽㄾ");
	final LevelContext ctx;
	
	Level42(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		for(int k = 0 ; k < word.length(); k++) {
			if(pattern.matched(word.charAt(k))) {
				// 받침만 표시해야 함
				mk.addRange(Difficulty.L42, k, 2, k + 1, 0);
			}
		}
		return mk;
	}

}
