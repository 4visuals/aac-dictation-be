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
		return ctx.setMark(Difficulty.L42, pattern, word);
	}

}
