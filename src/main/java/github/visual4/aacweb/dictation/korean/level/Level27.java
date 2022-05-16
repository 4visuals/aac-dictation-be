package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 25. 모음 'ㅢ'와 'ㅖ'와 'ㅒ' 소리 익히기
 * 
 * @author chminseo
 *
 */
public class Level27 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "ㅢㅖㅒ", "*");
	final LevelContext ctx;
	
	Level27(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		return ctx.setMark(Difficulty.L27, pattern, word);
	}
}
