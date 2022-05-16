package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 44. 소리는 같지만 글자와 뜻이 달라요 2
 * 
 * @author chminseo
 *
 */
public class Level44 implements ILevel {

	final Jamo prev = Jamo.pattern("*", "*", "ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ");
	final Jamo next = Jamo.pattern("ㅇ", "ㅣㅑㅕㅛㅠ", "*");
	final LevelContext ctx;
	
	Level44(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		
		return mk;
	}
}
