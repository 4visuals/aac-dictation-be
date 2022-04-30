package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 19. 끝에 오는 말: ~아요,~어요.
 * 
 * @author chminseo
 *
 */
public class Level19 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "*", "*");
	final LevelContext ctx;
	
	Level19(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		int [] pos = Levels.findSuffixPos(word, pattern, "아요");
		if (pos.length == 2) {
			mk.addRange(Difficulty.L19, pos[0] , pos[1]);
		}
		pos = Levels.findSuffixPos(word, pattern, "어요");
		if (pos.length == 2) {
			mk.addRange(Difficulty.L19, pos[0] , pos[1]);
		}
		return mk;
	}
}
