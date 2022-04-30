package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 42.  준말 표기 (ㅘ,ㅝ,ㅙ)
 * 주었다 → 줬다.
 * 나누었다 → 나눴다.
 * 되었다 → 됐다
 * 
 * @author chminseo
 */
public class Level42 implements ILevel {
	final Jamo pattern = Jamo.pattern("*", "ㅘㅙㅝ", "*");
	final LevelContext ctx;
	
	Level42(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		return ctx.setMark(Difficulty.L42, pattern, word);
	}

}
