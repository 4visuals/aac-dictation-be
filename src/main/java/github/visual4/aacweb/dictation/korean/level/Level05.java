package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 5. 받침 ㅇㄹㅁㄴ (초성, 중성 상관없음)
 * @author chminseo
 *
 */
public class Level05 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "*", "ㄴㄹㅁㅇ");
	final LevelContext ctx;
	
	Level05(LevelContext ctx) {
		this.ctx = ctx;
	}
	@Override
	public Mark eval(String word) {
		return this.ctx.setMark(Difficulty.L5, pattern, word);
	}

}
