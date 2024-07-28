package github.visual4.aacweb.dictation.korean.level;

import java.util.Arrays;
import java.util.List;

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

	List<String> words = Arrays.asList("졸였다", "조렸다", "있다가", "이따가", "닫히다","다치다","늘이다","느리다", "절이다", "저리다", "닫혔", "다쳤", "늘여", "절인");
	
	final LevelContext ctx;
	
	Level44(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		Levels.inject(mk, word, words, Difficulty.L44);
		return mk;
	}
}
