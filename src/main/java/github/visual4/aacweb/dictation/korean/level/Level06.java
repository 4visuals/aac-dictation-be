package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 6. 끝에 오는 말: ~ㄴ다.
 * @author chminseo
 *
 */
public class Level06 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "*", "ㄴ");
	final LevelContext ctx;
	
	Level06(LevelContext ctx) {
		this.ctx = ctx;
	}
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		int [] pos = Levels.findSuffixPos(word, pattern, "다");
		if (pos.length == 2) {
			mk.addRange(Difficulty.L6, pos[0] , pos[1]);
		}
		return mk;
	}
}
