package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 37. 소리를 닮아가요(1) 비음화
 * 앞글자의 종성이 바뀜
 * (뒤에 ㄴㅁㄹ이 오면 ㄱㄲㅋ→ㅇ, ㄷㅅㅆㅈㅊㅌㅂㅍ→ㄴㅁ 으로 바뀌어 소리나요)
 * 학년 => 항년
 * 막내 => 망내
 * @author chminseo
 *
 */
public class Level37 implements ILevel {

	final Jamo prev = Jamo.pattern("*", "*", "ㄱㄲㄳㄺㅋㄷㅅㅆㅈㅊㅌㅂㄼㄿㅄㅍㅎ");
	final Jamo next = Jamo.pattern("ㄴㄹㅁ", "*", "*");
	final LevelContext ctx;
	
	Level37(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		Levels.findAdjPos(word, prev, next, (range) -> {
			mk.addRange(Difficulty.L37, range[0], 2, range[1], -2);
		});
		return mk;
	}
}
