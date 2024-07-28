package github.visual4.aacweb.dictation.korean.level;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 18. 받침 ㄱㄲㅋ이 뒤로 넘어가 소리나요.
 * 
 * @author chminseo
 *
 */
public class Level18 implements ILevel {

	Set<String> exlusion = new HashSet<>(Arrays.asList("색연,박엿".split(",")));
	final Jamo prev = Jamo.pattern("*", "*", "ㄱㄲㅋ");
	final Jamo next = Jamo.pattern("ㅇ", "*", "*");
	final LevelContext ctx;
	
	public Level18(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		Levels.findAdjPos(word, prev, next, (range) -> {
			String token = word.substring(range[0], range[1]);
			if(!exlusion.contains(token)) {
				mk.addRange(Difficulty.L18, range[0], range[1]);
			}
		});
		return mk;
	}
}
