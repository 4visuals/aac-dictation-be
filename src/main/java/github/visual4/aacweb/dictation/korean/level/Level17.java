package github.visual4.aacweb.dictation.korean.level;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 15. 받침 ㄹㅁㄴ이 뒤로 넘어가 소리나요.
 * 
 * @author chminseo
 *
 */
public class Level17 implements ILevel {
	String pattern ="담요,꽃잎,철역,색연,박엿,낭여,알약,철역,휘발유,솔잎,한여름,용유,풍잎,서울역,올여름,정열,맨입,물엿,볼일,돌이끼,뒷일,열여,올여,발유";
	Set<String> exlusion = new HashSet<>(Arrays.asList(pattern.split(",")));
	
	final Jamo prev = Jamo.pattern("*", "*", "ㄴㄹㅁ");
	final Jamo next = Jamo.pattern("ㅇ", "*", "*");
	final LevelContext ctx;
	
	Level17(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		if("일일이".equals(word)) {
			mk.addRange(Difficulty.L17, 1, 3);
//			mk.removeRange(Difficulty.L17, 0, 3);
			return mk;
		}
		if(exlusion.contains(word)) {
			return mk;
		}
		Levels.findAdjPos(word, prev, next, (range) -> {
			// 전철역은 들어가면 안됨
			String token = word.substring(range[0], range[1]);
			if(!exlusion.contains(token)) {
				mk.addRange(Difficulty.L17, range[0], range[1]);	
			}
		});
		return mk;
	}
}
