package github.visual4.aacweb.dictation.korean.level;

import java.util.Arrays;
import java.util.List;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 48. 'ㄴ'이나 'ㄹ'를 넣어 발음해요.
 * (받침+첫소리모음 ㅣㅑㅕㅛㅠ일 때)
 * 
 * 표준 발음법 29항
 * 
 * @author chminseo
 *
 */
public class Level48 implements ILevel {
	String pattern ="담요,꽃잎,철역,색연,박엿,낭여,알약,철역,발유,솔잎,한여,용유,풍잎,울역,올여,정열,맨입,일일,물엿,볼일,돌이끼,뒷일,열여";
	List<String> words = Arrays.asList(pattern.split(","));
	
	final LevelContext ctx;
	
	Level48(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		inject(mk, word, words, Difficulty.L48);
		return mk;
	}
	static void inject(Mark mk, String sentence, List<String> words, Difficulty df) {
		for (int k = 0; k < words.size(); k++) {
			String word = words.get(k);
			int pos = sentence.indexOf(word);
			if(pos >= 0) {
				// pos에서 두단어까지인데(pos + 2) 뒷 단어의 모음까지 같이 포함시킴
				mk.addRange(df, pos, 2, pos + 2, -1);
				break;
			}
		}
	}
}
