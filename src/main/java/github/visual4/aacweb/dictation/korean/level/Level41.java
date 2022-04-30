package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 40. 받침 ㄼ ㄽ ㄾ
 * 
 * @author chminseo
 */
public class Level41 implements ILevel {
	final Jamo pattern = Jamo.pattern("*", "*", "ㄼㄽㄾ");
	final LevelContext ctx;
	
	Level41(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		// FIXME 일단 생략
		return ctx.findMark(word);
	}

}
