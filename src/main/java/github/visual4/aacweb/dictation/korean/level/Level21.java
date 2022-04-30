package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 21. 모음 'ㅝ'와 소리 익히기
 * 
 * @author chminseo
 *
 */
public class Level21 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "ㅝ", "*");
	final LevelContext ctx;
	
	Level21(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		return ctx.setMark(Difficulty.L21, pattern, word);
	}
}
