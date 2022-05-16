package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 10. 끝에 오는 말: ~ㅆ다.
 * 
 * @author chminseo
 *
 */
public class Level10 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "*", "ㅆ");
	final LevelContext ctx;
	
	public Level10(LevelContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		int [] pos = Levels.findSuffixPos(word, pattern, "다");
		if (pos.length == 2) {
			mk.addRange(Difficulty.L10, pos[0] , pos[1]);
		}
		return mk;
	}

}
