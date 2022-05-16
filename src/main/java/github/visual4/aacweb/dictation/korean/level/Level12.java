package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 12. 받침 ㅈㅊㅋ
 * @author chminseo
 *
 */
public class Level12 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "*", "ㅈㅊㅋ");
	final LevelContext ctx;
	
	Level12(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		return this.ctx.setMark(Difficulty.L12, pattern, word);
	}
}
