package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 7. 받침ㄱㄷㅂ
 * @author chminseo
 *
 */
public class Level07 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "*", "ㄱㄷㅂ");
	final LevelContext ctx;
	
	Level07(LevelContext ctx) {
		this.ctx = ctx;
	}
	@Override
	public Mark eval(String word) {
		return this.ctx.setMark(Difficulty.L07, pattern, word);
	}
}
