package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 8. 받침 ㅅㅆ
 * @author chminseo
 *
 */
public class Level8 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "*", "ㅅㅆ");
	final LevelContext ctx;
	
	Level8(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		return this.ctx.setMark(Difficulty.L8, pattern, word);
	}
}
