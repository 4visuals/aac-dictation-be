package github.visual4.aacweb.dictation.korean.level;

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

	final Jamo pattern = Jamo.pattern("*", "*", "ㄷㅅㅆㅈㅊㅌ");
	final LevelContext ctx;
	
	Level32(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		for(int k = 0 ; k < word.length(); k++) {
			if(pattern.matched(word.charAt(k))) {
				// 받침만 표시해야 함
				mk.addRange(Difficulty.L32, k, 2, k + 1, 0);
			}
		}
		return mk;
	}
}
