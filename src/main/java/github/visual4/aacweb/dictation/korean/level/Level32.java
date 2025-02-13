package github.visual4.aacweb.dictation.korean.level;

import java.util.Set;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 30. 받침 ㄷㅅㅆㅈㅊㅌ은 읽을 때 ㄷ으로 소리나요.
 * 
 * @author chminseo
 *
 */
public class Level32 implements ILevel {

//	final Jamo pattern = Jamo.pattern("*", "*", "ㄷㅅㅆㅈㅊㅌ");
	final Jamo prev = Jamo.pattern("*", "*", "ㄷㅅㅆㅈㅊㅌ");
	final Set<Jamo> nexts = Set.of(Jamo.pattern("ㄱㄲㄷㄸㅂㅃㅅㅆㅈㅉㅊㅋㅌㅍ", "*", "*"), Jamo.SPACE);
	final LevelContext ctx;
	
	Level32(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		Levels.findAdjPos(word, prev, nexts,
			range ->mk.addRange(Difficulty.L32, range[0], 2, range[1], -3)
		);
		int lastIdx = word.length() - 1 ;
		if(prev.matched(word.charAt(lastIdx))) {
			mk.addRange(Difficulty.L32, lastIdx, 2, lastIdx + 1, -1);
		}
		
		return mk;
	}
}
