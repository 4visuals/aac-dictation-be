package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 30. 받침 ㅂㅍ은 읽을 때 ㅂ으로 소리나요
 * 
 * 어말) 종성이 ㅂ,ㅍ으로 끝나고 다음 글자의 초성이 ㄱㄷㅂㅅㅈ으로 시작하는 경우만
 * @author chminseo
 *
 */
public class Level30 implements ILevel {

	final Jamo prev = Jamo.pattern("*", "*", "ㅂㅍ");
	final Jamo next = Jamo.pattern("ㄱㄲㄷㄸㅂㅃㅅㅆㅈㅉㅊㅋㅌㅍ", "*", "*");
	final LevelContext ctx;
	
	Level30(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		Levels.findAdjPos(word, prev, next,
			range ->mk.addRange(Difficulty.L30, range[0], 2, range[1], -3)
		);
		int lastIdx = word.length() - 1 ;
		if(prev.matched(word.charAt(lastIdx))) {
			mk.addRange(Difficulty.L30, lastIdx, 2, lastIdx + 1, -3);
		}
		return mk;
	}
}
