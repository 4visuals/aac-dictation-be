package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 24. 모음 'ㅙ'와 'ㅞ' 소리 익히기
 * 
 * @author chminseo
 *
 */
public class Level26 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "ㅙㅞ", "*");
	final LevelContext ctx;
	
	Level26(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		return ctx.setMark(Difficulty.L26, pattern, word);
	}
}
