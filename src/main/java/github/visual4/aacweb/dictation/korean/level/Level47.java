package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 47. '-(는)대'와 '-(는)데'를 구분해 써요.
 * 
 * @author chminseo
 */
public class Level47 implements ILevel {
	// final Jamo pattern = Jamo.pattern("*", "ㅘㅙㅝ", "*");
	final LevelContext ctx;
	
	Level47(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		
		int k = 0;
		for( ; k < word.length() - 1; k++) {
			char c = word.charAt(k);
			if ("대데".indexOf(c) >= 0) {
				char next = word.charAt(k+1);
				if (". ?".indexOf(next) >= 0) {
					mk.addRange(Difficulty.L47, k, k+1);
				}
			}
		}
		if ("대데".indexOf(word.charAt(k)) >= 0) {
			mk.addRange(Difficulty.L47, k, k+1);
		}
		return mk;
	}

}
