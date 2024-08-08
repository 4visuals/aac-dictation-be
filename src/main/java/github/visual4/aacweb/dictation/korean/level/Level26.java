package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 24. 모음 'ㅙ'와 'ㅞ' 소리 익히기
 * 
 * @author chminseo
 *
 */
public class Level26 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "ㅙㅞ", "*");
	final LevelContext ctx;
	
	Level26(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		for(int k = 0 ; k < word.length(); k++) {
			if(pattern.matched(word.charAt(k))) {
				// 초성까지 같이 표시함
				mk.addRange(Difficulty.L26, k, 0, k + 1, -1);
			}
		}
		return mk;			
	}
}
