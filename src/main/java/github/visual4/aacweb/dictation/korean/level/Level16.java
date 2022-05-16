package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 16. 모음  'ㅐ'와 'ㅔ' 구분2
 * 
 * @author chminseo
 *
 */
public class Level16 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "ㅐㅔ", "*");
	final LevelContext ctx;
	
	Level16(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		return this.ctx.setMark(Difficulty.L16, pattern, word);
	}
}
