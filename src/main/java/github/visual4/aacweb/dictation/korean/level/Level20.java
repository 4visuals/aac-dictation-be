package github.visual4.aacweb.dictation.korean.level;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 18. 받침 ㄷㅅㅆㅈㅊㅌ이 뒤로 넘어가 소리나요.
 * 
 * @author chminseo
 *
 */
public class Level20 implements ILevel {
	Set<String> exlusion = new HashSet<>(Arrays.asList("꽃잎".split(",")));
	final Jamo prev = Jamo.pattern("*", "*","ㄷㅅㅆㅈㅊㅌ");
	final Jamo next = Jamo.pattern("ㅇ", "*", "*");
	final LevelContext ctx;
	
	Level20(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		if("꽃잎".equals(word)) {
			return mk;
		}
		Levels.findAdjPos(word, prev, next, range -> {
			String token = word.substring(range[0], range[1]);
			if(!exlusion.contains(token)) {
				// 앞글자 종성과 뒷글자 초성만
				mk.addRange(Difficulty.L20, range[0], 2, range[1], -2);				
			}
		});
		return mk;
	}
}
