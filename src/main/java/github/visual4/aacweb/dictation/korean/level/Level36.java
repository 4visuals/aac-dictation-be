package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 36. ㄷㅌ이 ㅈㅊ으로 바뀌어 소리나요. 구개음화
 * (ㄷㅌ+ㅣㅑㅕㅛㅠ→ㅈㅊ)
 * 
 * @author chminseo
 *
 */
public class Level36 implements ILevel {

	public static final Jamo prev = Jamo.pattern("*", "*", "ㄷㅌ");
	public static final Jamo next = Jamo.pattern("ㅇㅎ", "ㅣㅕ", "*");
	final LevelContext ctx;
	
	Level36(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		Levels.findAdjPos(word, prev, next, (range) -> {
			mk.addRange(Difficulty.L36, range[0], 2, range[1], -2);
		});
		return mk;
	}
}
