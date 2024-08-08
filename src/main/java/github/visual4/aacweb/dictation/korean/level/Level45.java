package github.visual4.aacweb.dictation.korean.level;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 45. 소리는 같지만 글자와 뜻이 달라요 3
 * 
 * @author chminseo
 *
 */
public class Level45 implements ILevel {
	List<String> exclusion = 
			Arrays.asList("올라갔다 기다렸습니다 들어갔다 잠갔다 기다렸다".split(" ")); 
	String pattern ="달이다,다리다,식히다,시키다,갔다,같다,낫다,낮다,거름,걸음,다렸,달였,시켰다,식혔다";
	List<String> words = Arrays.asList(pattern.split(","));
	final LevelContext ctx;
	
	Level45(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		for (String exp : exclusion) {
			if(word.contains(exp)) {
				return mk;
			}
		}
		Levels.inject(mk, word, words, Difficulty.L45);
		return mk;
	}
}
