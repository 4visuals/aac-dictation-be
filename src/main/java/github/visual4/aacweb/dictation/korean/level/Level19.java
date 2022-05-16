package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 19. 받침 ㅂㅍ이 뒤로 넘어가 소리나요.
 * 
 * @author chminseo
 *
 */
public class Level19 implements ILevel {

	final Jamo prev = Jamo.pattern("*", "*","ㅂㅍ");
	final Jamo next = Jamo.pattern("ㅇ", "*", "*");
	final LevelContext ctx;
	
	Level19(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		Levels.findAdjPos(word, prev, next, (range) -> {
			mk.addRange(Difficulty.L19, range[0], range[1]);
		});
		return mk;
	}
}
