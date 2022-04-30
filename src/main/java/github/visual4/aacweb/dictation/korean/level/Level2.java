package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 2. 자음(ㅇㄱㄴㄷㄹㅁㅂㅅㅈ) + 모음(ㅑㅕㅛㅠ) + 받침 없음
 * @author chminseo
 *
 */
public class Level2 implements ILevel {

	final Jamo pattern = Jamo.pattern("ㅇㄱㄴㄷㄹㅁㅂㅅㅈ", "ㅑㅕㅛㅠ", "_");
	final LevelContext ctx;
	
	Level2(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		return ctx.setMark(Difficulty.L2, pattern, word);
	}

}
