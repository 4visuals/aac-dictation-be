package github.visual4.aacweb.dictation.korean.level;

import java.util.Arrays;
import java.util.List;

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
	List<String> words = Arrays.asList(
			"붙이다", "부치다",
			"맞히다", "마치다", "걷히다","거치다","받치다","바치다", "반듯이", "반드시", "붙이", "부치","바쳤", "받쳤", "받쳐", "거쳐", "걷히","맞혔", "마쳤", "맞혀");
	final LevelContext ctx;
	
	Level43(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		
		Levels.inject(mk, word, words, Difficulty.L43);
		
		return mk;
	}
}
