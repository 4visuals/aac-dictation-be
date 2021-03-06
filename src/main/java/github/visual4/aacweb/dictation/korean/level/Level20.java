package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 18. 받침 ㄷㅅㅆㅈㅊㅌ이 뒤로 넘어가 소리나요.
 * 
 * @author chminseo
 *
 */
public class Level20 implements ILevel {

	final Jamo prev = Jamo.pattern("*", "*","ㄷㅅㅆㅈㅊㅌ");
	final Jamo next = Jamo.pattern("ㅇ", "*", "*");
	final LevelContext ctx;
	
	Level20(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		Levels.findAdjPos(word, prev, next, (range) -> {
			mk.addRange(Difficulty.L20, range[0], range[1]);
		});
		return mk;
	}
}
