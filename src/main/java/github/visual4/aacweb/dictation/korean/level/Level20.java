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
	/**
	 * see https://github.com/4visuals/aac-dictation-be/issues/7
	 * 
	 * 36단계에 해당하면 20단계는 표시하지 않음.
	 */
	private boolean checkIfLevel36(String word) {
		int [] lvl36 = {0};
		Levels.findAdjPos(word,
				Level36.prev,
				Level36.next,
				range -> lvl36[0]++);
		return lvl36[0] > 0;
	}
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		if("꽃잎".equals(word)) {
			return mk;
		}
		Levels.findAdjPos(word, prev, next, range -> {
			String token = word.substring(range[0], range[1]);
			boolean lvl36 = checkIfLevel36(token);
			if(!lvl36 && !exlusion.contains(token)) {
				// 앞글자 종성과 뒷글자 초성만
				mk.addRange(Difficulty.L20, range[0], 2, range[1], -2);				
			}
		});
		return mk;
	}
}
