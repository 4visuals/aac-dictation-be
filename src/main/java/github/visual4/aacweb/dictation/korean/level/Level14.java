package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 14. 모음 'ㅘ'소리 익히기
 * @author chminseo
 *
 */
public class Level14 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "ㅘ", "*");
	final LevelContext ctx;
	
	Level14(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		for(int k = 0 ; k < word.length(); k++) {
			if(pattern.matched(word.charAt(k))) {
				// 중성만 표시해야 함
				mk.addRange(Difficulty.L14, k, 0, k + 1, -1);
			}
		}
		return mk;
	}
}
