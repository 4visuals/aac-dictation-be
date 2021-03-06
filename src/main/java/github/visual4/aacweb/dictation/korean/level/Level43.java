package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 43. 소리는 같지만 글자와 뜻이 달라요 1
 * 
 * @author chminseo
 */
public class Level43 implements ILevel {
	// final Jamo pattern = Jamo.pattern("*", "ㅘㅙㅝ", "*");
	final LevelContext ctx;
	
	Level43(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		
		return mk;
	}

}
