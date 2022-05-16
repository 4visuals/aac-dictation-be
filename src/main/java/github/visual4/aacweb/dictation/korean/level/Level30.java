package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 30. 받침 ㅂㅍ은 읽을 때 ㅂ으로 소리나요
 * 
 * @author chminseo
 *
 */
public class Level30 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "*", "ㅂㅍ");
	final LevelContext ctx;
	
	Level30(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		return ctx.setMark(Difficulty.L30, pattern, word);
	}
}
