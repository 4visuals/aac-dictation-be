package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 5. 받침 ㅇㄹㅁㄴ (초성, 중성 상관없음)
 * @author chminseo
 *
 */
public class Level05 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "*", "ㄴㄹㅁㅇ");
	final LevelContext ctx;
	
	public Level05(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		for(int k = 0 ; k < word.length(); k++) {
			if(pattern.matched(word.charAt(k))) {
				// 중성만 표시해야 함
				mk.addRange(Difficulty.L05, k, 2, k + 1, 0);
			}
		}
		return mk;
	}
}
