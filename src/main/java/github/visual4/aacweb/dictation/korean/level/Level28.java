package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 28. 음절 끝소리가 ㅂ으로 소리 나도 원래 받침 ㅍ으로 써요.
 * 
 * @author chminseo
 *
 */
public class Level28 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "*", "ㅍ");
	final LevelContext ctx;
	
	Level28(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		return ctx.setMark(Difficulty.L28, pattern, word);
	}
}
