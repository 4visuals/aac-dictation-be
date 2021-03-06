package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 23. 모음 'ㅚ'와  'ㅟ' 소리 익히기
 * 
 * @author chminseo
 *
 */
public class Level25 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "ㅚㅟ", "*");
	final LevelContext ctx;
	
	Level25(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		return ctx.setMark(Difficulty.L25, pattern, word);
	}
}
