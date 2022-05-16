package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 33. 된소리로 바뀌어요
 * (앞의 받침소리 때문에뒤에 있는 첫 자음이 된소리로 바뀜 ㄱㄷㅂㅅㅈ→ㄲㄸㅃㅆㅉ)
 * 
 * 
 * 제6장 된소리되기,
 * 제23항
 * 받침 
 * 'ㄱ(ㄲ, ㅋ, ㄳ, ㄺ), ㄷ(ㅅ, ㅆ, ㅈ, ㅊ, ㅌ), ㅂ(ㅍ, ㄼ, ㄿ, ㅄ)'
 * 뒤에 연결되는 'ㄱ, ㄷ, ㅂ, ㅅ, ㅈ'은 된소리로 발음한다.
 * 제24항
 * 어간 받침 ‘ㄴ(ㄵ), ㅁ(ㄻ)’ 뒤에 결합되는 어미의 첫소리 ‘ㄱ, ㄷ, ㅅ, ㅈ’은 된소리로 발음한다.
 * 
 * FIXME 국립 국어원의 규칙과 다름. 확인 필요
 * @author chminseo
 *
 */
public class Level33 implements ILevel {

	final Jamo prev = Jamo.pattern("*", "*", "ㄱㄴㄲㄳㄵㄷㄹㄺㄻㄼㄿㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍ");
	final Jamo next = Jamo.pattern("ㄱㄷㅂㅅㅈ", "*", "*");
	final LevelContext ctx;
	
	Level33(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		Levels.findAdjPos(word, prev, next, (range) -> {
			mk.addRange(Difficulty.L33, range[0], range[1]);
		});
		return mk;
	}
}
