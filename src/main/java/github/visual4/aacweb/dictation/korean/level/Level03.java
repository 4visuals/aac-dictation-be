package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 3. 자음(ㅊㅋㅌㅍㅎ) + 모음 + 받침 없음
 * 
 * @author chminseo
 */
public class Level03 implements ILevel {
	final Jamo pattern = Jamo.pattern("ㅊㅋㅌㅍㅎ", "ㅏㅑㅓㅕㅗㅛㅜㅠㅡㅣ", "_");
	final LevelContext ctx;
	
	Level03(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		return ctx.setMark(Difficulty.L3, pattern, word);
	}

}
