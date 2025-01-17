package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 35. ㅎ이 다음에 오면 거센소리로 바뀌어요.
 * (ㄱㄷㅂㅅㅈ+ㅎ→ㅋㅌㅍㅊ)
 * 
 * 4장 받침의 발음, 12항 받침 'ㅎ'의 발음[붙임 1]
 * https://kornorms.korean.go.kr/regltn/regltnView.do?regltn_code=0002&regltn_no=346#a405
 * 
 * FIXME 국립 국어원의 규칙과 다름. 확인 필요
 * @author chminseo
 *
 */
public class Level35 implements ILevel {

	final Jamo prev = Jamo.pattern("*", "*", "ㄱㄷㅂㅈ");
	final Jamo next = Jamo.pattern("ㅎ", "*", "*");
	final LevelContext ctx;
	
	Level35(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		Levels.findAdjPos(word, prev, next, (range) -> {
			mk.addRange(Difficulty.L35, range[0], 2, range[1], -2);
		});
		return mk;
	}
}
