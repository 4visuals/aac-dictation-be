package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 9. 받침 ㅅㅆ
 * @author chminseo
 *
 */
public class Level09 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "*", "ㅅㅆ");
	final LevelContext ctx;
	
	Level09(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		for(int k = 0 ; k < word.length(); k++) {
			if(pattern.matched(word.charAt(k))) {
				mk.addRange(Difficulty.L09, k, 2, k + 1, 0);
			}
		}
		return mk;
	}
}
