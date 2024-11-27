package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 32. ㅎ뒤엔 거센소리로 바뀌어요.
 * (ㅎ받침+ㄱㄷㅈ→ㅋㅌㅍㅊ)
 * ex) 좋다 => 조타
 *     낳다 => 나타
 *     좋게 => 조케
 *     하얗다 => 하야타
 * 
 * 4장 받침의 발음, 12항 받침 'ㅎ'의 발음
 * https://kornorms.korean.go.kr/regltn/regltnView.do?regltn_code=0002&regltn_no=346#a405
 * 
 * FIXME 국립 국어원의 규칙과 다름. 확인 필요
 * @author chminseo
 *
 */
public class Level34 implements ILevel {

	final Jamo prev = Jamo.pattern("*", "*", "ㅎ");
	final Jamo next = Jamo.pattern("ㄱㄷㅈ", "*", "*");
	final LevelContext ctx;
	
	Level34(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		Levels.findAdjPos(word, prev, next, (range) -> {
			mk.addRange(Difficulty.L34, range[0], 2, range[1], -2);
		});
		return mk;
	}
}
