package github.visual4.aacweb.dictation.korean.level;

import java.util.Arrays;
import java.util.List;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 46.  줄여서 써요 (ㅘ,ㅝ,ㅙ)
 * 주었다 → 줬다.
 * 나누었다 → 나눴다.
 * 되었다 → 됐다
 * 
 * @author chminseo
 */
public class Level46 implements ILevel {
	String inclusion ="꿔,꿨,눠,눴,돼,됐,둬,뒀,봐,봤,줘,줬,춰,췄";
	List<String> words = Arrays.asList(inclusion.split(","));
	
	final Jamo prev = Jamo.pattern("*", "ㅘㅙㅝ", "*");
	final Jamo next = Jamo.pattern("*", "*", "*");
	
	final LevelContext ctx;
	
	Level46(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		if(word.contains("돼지")) {
			return mk;
		}
		words.forEach(wd -> {
			int pos = word.indexOf(wd);
			if(pos >= 0) {
				mk.addRange(Difficulty.L46, pos, pos + 1);
			}
		});
		return mk;
	}

}
