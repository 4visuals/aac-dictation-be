package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 16. 받침 ㄱㄲㅋ이 뒤로 넘어가 소리나요.
 * 
 * @author chminseo
 *
 */
public class Level16 implements ILevel {

	final Jamo prev = Jamo.pattern("*", "*", "ㄱㄲㅋ");
	final Jamo next = Jamo.pattern("ㅇ", "*", "*");
	final LevelContext ctx;
	
	Level16(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		Levels.findAdjPos(word, prev, next, (range) -> {
			mk.addRange(Difficulty.L16, range[0], range[1]);
		});
		return mk;
	}
}
