package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 15. 모음  'ㅐ'와 'ㅔ' 구분 1
 * 
 * @author chminseo
 *
 */
public class Level15 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "ㅐㅔ", "*");
	final LevelContext ctx;
	
	Level15(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		return this.ctx.setMark(Difficulty.L15, pattern, word);
	}
}
