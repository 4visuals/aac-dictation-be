package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 21. 끝에 오는 말: ~아요,~어요.
 * 
 * @author chminseo
 *
 */
public class Level21 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "*", "*");
	final LevelContext ctx;
	
	Level21(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		int [] pos = Levels.findSuffixPos(word, pattern, "아요");
		/**
		 * pos[0]은 "X아요"에서 X까지 포함하고 있음. 
		 * 글자 'X'의 종성까지 규칙에 포함시킴
		 */
		if (pos.length == 2) {
			mk.addRange(Difficulty.L21, pos[0], 2, pos[1], 0);
		}
		pos = Levels.findSuffixPos(word, pattern, "어요");
		/**
		 * pos[0]은 "X어요"에서 X까지 포함하고 있음. 
		 * 글자 'X'의 종성까지 규칙에 포함시킴
		 */
		if (pos.length == 2) {
			mk.addRange(Difficulty.L21, pos[0], 2, pos[1], 0);
		}
		return mk;
	}
}
