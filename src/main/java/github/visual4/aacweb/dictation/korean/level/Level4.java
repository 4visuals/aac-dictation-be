package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 4. 자음(ㄲㄸㅃㅆㅉ)+모음
 * 
 * @author chminseo
 *
 */
public class Level4 implements ILevel{

	final Jamo pattern = Jamo.pattern("ㄲㄸㅃㅆㅉ", "*", "_");
	final LevelContext ctx;
	
	Level4(LevelContext ctx) {
		this.ctx = ctx;
	}
	@Override
	public Mark eval(String word) {
		return this.ctx.setMark(Difficulty.L4, pattern, word);
	}

}
