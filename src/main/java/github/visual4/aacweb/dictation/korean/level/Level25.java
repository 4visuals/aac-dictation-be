package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 23. 모음 'ㅚ'와  'ㅟ' 소리 익히기
 * 
 * @author chminseo
 *
 */
public class Level25 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "ㅚㅟ", "*");
	final LevelContext ctx;
	
	Level25(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		for(int k = 0 ; k < word.length(); k++) {
			if(pattern.matched(word.charAt(k))) {
				// 초성까지 같이 표시함
				mk.addRange(Difficulty.L25, k, 0, k + 1, -1);
			}
		}
		return mk;
	}
}
