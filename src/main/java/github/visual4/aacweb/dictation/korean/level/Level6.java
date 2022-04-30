package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 6. 받침ㄱㄷㅂ (초성, 중성 상관없음)
 * @author chminseo
 *
 */
public class Level6 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "*", "ㄱㄷㅂ");
	final LevelContext ctx;
	
	Level6(LevelContext ctx) {
		this.ctx = ctx;
	}
	@Override
	public Mark eval(String word) {
		return this.ctx.setMark(Difficulty.L6, pattern, word);
	}
}
