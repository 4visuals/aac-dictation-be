package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 36. ㄷㅌ이 ㅈㅊ으로 바뀌어 소리나요.
 * (ㄷㅌ+ㅣㅑㅕㅛㅠ→ㅈㅊ)
 * 
 * @author chminseo
 *
 */
public class Level36 implements ILevel {

	final Jamo prev = Jamo.pattern("*", "*", "ㄷㅌ");
	final Jamo next = Jamo.pattern("ㅇ", "ㅣㅑㅕㅛㅠ", "*");
	final LevelContext ctx;
	
	Level36(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		Levels.findAdjPos(word, prev, next, (range) -> {
			mk.addRange(Difficulty.L36, range[0], range[1]);
		});
		return mk;
	}
}
