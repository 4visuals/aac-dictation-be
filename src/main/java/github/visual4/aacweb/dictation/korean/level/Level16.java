package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 16. 모음  'ㅐ'와 'ㅔ' 구분2
 * 
 * @author chminseo
 *
 */
public class Level16 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "ㅐㅔ", "*");
	final LevelContext ctx;
	
	Level16(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		for(int k = 0 ; k < word.length(); k++) {
			if(pattern.matched(word.charAt(k))) {
				// 중성만 표시해야 함
				mk.addRange(Difficulty.L16, k, 0, k + 1, -1);
			}
		}
		return mk;
	}
}
