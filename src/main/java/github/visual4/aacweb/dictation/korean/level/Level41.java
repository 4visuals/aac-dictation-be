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
		return ctx.setMark(Difficulty.L41, pattern, word);
	}

}
