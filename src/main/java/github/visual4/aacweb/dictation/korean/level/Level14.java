package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 14. 모음  'ㅐ'와 'ㅔ' 구분
 * 
 * @author chminseo
 *
 */
public class Level14 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "ㅐㅔ", "*");
	final LevelContext ctx;
	
	Level14(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		return this.ctx.setMark(Difficulty.L14, pattern, word);
	}
}
