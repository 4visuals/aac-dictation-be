package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 12. 받침 ㅈㅊㅋ
 * @author chminseo
 *
 */
public class Level12 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "*", "ㅈㅊㅋ");
	final LevelContext ctx;
	
	Level12(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
//		return this.ctx.setMark(Difficulty.L12, pattern, word);
		Mark mk = ctx.findMark(word);
		for(int k = 0 ; k < word.length(); k++) {
			if(pattern.matched(word.charAt(k))) {
				mk.addRange(Difficulty.L12, k, 2, k + 1, 0);
			}
		}
		return mk;
	}
}
