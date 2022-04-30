package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 29. 음절 끝소리가 ㄱ으로 소리 나도 원래 받침 ㅋㄲ으로 써요.
 * 
 * @author chminseo
 *
 */
public class Level29 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "*", "ㅋㄲ");
	final LevelContext ctx;
	
	Level29(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		return ctx.setMark(Difficulty.L29, pattern, word);
	}
}
