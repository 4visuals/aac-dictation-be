package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 13. 받침 ㅌㅍㅎ
 * @author chminseo
 *
 */
public class Level13 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "*", "ㅌㅍㅎ");
	final LevelContext ctx;
	
	Level13(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		for(int k = 0 ; k < word.length(); k++) {
			if(pattern.matched(word.charAt(k))) {
				mk.addRange(Difficulty.L13, k, 2, k + 1, 0);
			}
		}
		return mk;
	}
}
