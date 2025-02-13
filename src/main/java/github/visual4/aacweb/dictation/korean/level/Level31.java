package github.visual4.aacweb.dictation.korean.level;

import java.util.Set;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 31. 받침 ㄱㅋㄲ은 읽을 때 ㄱ으로 소리나요.
 * 
 * ㄱㄷㅂㅅㅈ
 * ㄲㄸㅃㅆㅉ
 * ㅋㅌㅍ_ㅊ  
 * @author chminseo
 *
 */
public class Level31 implements ILevel {

	final Jamo prev = Jamo.pattern("*", "*", "ㄱㄲㅋ");
//	final Jamo next = Jamo.pattern("ㄱㄷㅂㅅㅈ", "*", "*");
	final Set<Jamo> nexts = Set.of(Jamo.pattern("ㄱㄲㄷㄸㅂㅃㅅㅆㅈㅉㅊㅋㅌㅍ", "*", "*"), Jamo.SPACE);
	final LevelContext ctx;
	
	Level31(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		Levels.findAdjPos(word, prev, nexts,
			range ->mk.addRange(Difficulty.L31, range[0], 2, range[1], -3)
		);
		int lastIdx = word.length() - 1 ;
		if(prev.matched(word.charAt(lastIdx))) {
			mk.addRange(Difficulty.L31, lastIdx, 2, lastIdx + 1, -1);
		}
		return mk;
	}
}
