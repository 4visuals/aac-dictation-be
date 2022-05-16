package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 31. 받침 ㄱㅋㄲ은 읽을 때 ㄱ으로 소리나요.
 * 
 * @author chminseo
 *
 */
public class Level31 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "*", "ㄱㅋㄲ");
	final LevelContext ctx;
	
	Level31(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		return ctx.setMark(Difficulty.L31, pattern, word);
	}
}
