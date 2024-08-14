package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 2. 자음(ㄱㄴㄷㄹㅁㅂㅅㅇㅈ) + 모음(ㅑㅕㅛㅠ) + 받침 없음
 * @author chminseo
 *
 */
public class Level02 implements ILevel {

	final Jamo pattern = Jamo.pattern("ㅇㄱㄴㄷㄹㅁㅂㅅㅈ", "ㅑㅕㅛㅠ", "*");
	final LevelContext ctx;
	
	public Level02(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		for(int k = 0 ; k < word.length(); k++) {
			if(pattern.matched(word.charAt(k))) {
				// 중성만 표시해야 함
				mk.addRange(Difficulty.L02, k, 0, k + 1, -1);
			}
		}
		return mk;
	}

}
