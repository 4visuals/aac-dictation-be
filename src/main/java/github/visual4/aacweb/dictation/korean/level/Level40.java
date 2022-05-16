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
		return ctx.setMark(Difficulty.L40, pattern, word);
	}

}
