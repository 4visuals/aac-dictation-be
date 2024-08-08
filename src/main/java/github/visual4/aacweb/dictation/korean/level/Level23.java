package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 21. 모음 'ㅝ'와 소리 익히기
 * 
 * @author chminseo
 *
 */
public class Level23 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "ㅝ", "*");
	final LevelContext ctx;
	
	Level23(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		for(int k = 0 ; k < word.length(); k++) {
			if(pattern.matched(word.charAt(k))) {
				// 초성 + 'ㅘ'만 표시해야 함
				mk.addRange(Difficulty.L23, k, 0, k + 1, -1);
			}
		}
		return mk;
	}
}
