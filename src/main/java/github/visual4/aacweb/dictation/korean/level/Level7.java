package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 7. 끝에 오는 말 : ~ㅂ니다.
 * 
 * @author chminseo
 *
 */
public class Level7 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "*", "ㅂ");
	final LevelContext ctx;
	
	public Level7(LevelContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		int [] pos = Levels.findSuffixPos(word, pattern, "니다");
		if (pos.length == 2) {
			mk.addRange(Difficulty.L7, pos[0] , pos[1]);
		}
		return mk;
	}

}
