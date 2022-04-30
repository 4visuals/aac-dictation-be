package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 22. 끝에 오는 말: ~ㅝ요.
 * 
 * @author chminseo
 *
 */
public class Level22 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "ㅝ", "_");
	final LevelContext ctx;
	
	Level22(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		int [] pos = Levels.findSuffixPos(word, pattern, "요");
		if (pos.length == 2) {
			mk.addRange(Difficulty.L22, pos[0] , pos[1]);
		}
		return mk;
	}
}
