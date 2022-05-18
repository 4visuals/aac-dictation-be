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

	final Jamo pattern = Jamo.pattern("ㅇㄱㄴㄷㄹㅁㅂㅅㅈ", "ㅑㅕㅛㅠ", "_");
	final LevelContext ctx;
	
	Level02(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		return ctx.setMark(Difficulty.L02, pattern, word);
	}

}
