package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 15. 받침 ㄹㅁㄴ이 뒤로 넘어가 소리나요.
 * 
 * @author chminseo
 *
 */
public class Level15 implements ILevel {

	final Jamo prev = Jamo.pattern("*", "*", "ㄴㄹㅁ");
	final Jamo next = Jamo.pattern("ㅇ", "*", "*");
	final LevelContext ctx;
	
	Level15(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		Levels.findAdjPos(word, prev, next, (range) -> {
			mk.addRange(Difficulty.L15, range[0], range[1]);
		});
		return mk;
	}
}
