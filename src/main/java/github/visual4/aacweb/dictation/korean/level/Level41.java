package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 41. 받침 ㄳ ㄵ ㅄ
 * 
 * @author chminseo
 */
public class Level41 implements ILevel {
	final Jamo pattern = Jamo.pattern("*", "*", "ㄺㄻㄿ");
	final LevelContext ctx;
	
	Level41(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		for(int k = 0 ; k < word.length(); k++) {
			if(pattern.matched(word.charAt(k))) {
				// 받침만 표시해야 함
				mk.addRange(Difficulty.L41, k, 2, k + 1, 0);
			}
		}
		return mk;
	}

}
