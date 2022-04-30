package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 38. 받침 ㄳ ㄵ ㅄ
 * 
 * @author chminseo
 */
public class Level39 implements ILevel {
	final Jamo pattern = Jamo.pattern("*", "*", "ㄺㄻㄿ");
	final LevelContext ctx;
	
	Level39(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		return ctx.setMark(Difficulty.L39, pattern, word);
	}

}
