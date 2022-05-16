package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 13. 받침 ㅌㅍㅎ
 * @author chminseo
 *
 */
public class Level13 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "*", "ㅌㅍㅎ");
	final LevelContext ctx;
	
	Level13(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		return this.ctx.setMark(Difficulty.L13, pattern, word);
	}
}
