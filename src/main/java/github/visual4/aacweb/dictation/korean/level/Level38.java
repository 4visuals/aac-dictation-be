package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 38. 받침 ㄳ ㄵ ㅄ
 * 
 * @author chminseo
 */
public class Level38 implements ILevel {
	final Jamo pattern = Jamo.pattern("*", "*", "ㄳㄵㅄ");
	final LevelContext ctx;
	
	Level38(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		return ctx.setMark(Difficulty.L38, pattern, word);
	}

}
