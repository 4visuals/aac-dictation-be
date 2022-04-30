package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 30. 음절 끝소리가 ㄷ으로 소리 나도 원래 받침 ㅅㅆㅈㅊㅌ으로 써요.
 * 
 * @author chminseo
 *
 */
public class Level30 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "*", "ㄷㅅㅆㅈㅊㅌ");
	final LevelContext ctx;
	
	Level30(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		return ctx.setMark(Difficulty.L30, pattern, word);
	}
}
