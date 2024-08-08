package github.visual4.aacweb.dictation.korean.level;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 33. 된소리로 바뀌어요
 * (앞의 받침소리 때문에뒤에 있는 첫 자음이 된소리로 바뀜 ㄱㄷㅂㅅㅈ→ㄲㄸㅃㅆㅉ)
 * 
 * 
 * 제6장 된소리되기,
 * 제23항
 * 받침 
 * 'ㄱ(ㄲ, ㅋ, ㄳ, ㄺ), ㄷ(ㅅ, ㅆ, ㅈ, ㅊ, ㅌ), ㅂ(ㅍ, ㄼ, ㄿ, ㅄ)'
 * 뒤에 연결되는 'ㄱ, ㄷ, ㅂ, ㅅ, ㅈ'은 된소리로 발음한다.
 * 제24항
 * 어간 받침 ‘ㄴ(ㄵ), ㅁ(ㄻ)’ 뒤에 결합되는 어미의 첫소리 ‘ㄱ, ㄷ, ㅅ, ㅈ’은 된소리로 발음한다.
 * 
 * FIXME 국립 국어원의 규칙과 다름. 확인 필요
 * @author chminseo
 *
 */
public class Level33 implements ILevel {
	String caseText = "갈색,갈지,강가,개[울가],글자,[금덩]이,[눈사]람,달빛,들새,마[술사],몸집,물감,물[건값],[물고]기,물병,물속,[밀가]루,밤길,버[팀대],별빛,보[낼지]도,보[름달],불빛,빨대,산새,[손바]닥,손등,[손전]등,시[골집],아[침밥],열쇠,[열심]히,용돈,[일주]일,장[난감],장바구니,줄[넘기],창가";
	Map<String, Token> parts = new HashMap<>();
	Set<String> tokenSet;
	final Jamo prev = Jamo.pattern("*", "*", "ㄱㄷㅂㅅㅆㅈㅊㅋㅌㅍㄲㄳㄺㄼㄿㅄ");
	final Jamo next = Jamo.pattern("ㄱㄷㅂㅅㅈ", "*", "*");
	final LevelContext ctx;
	
	Level33(LevelContext ctx) {
		this.ctx = ctx;
		String [] patterns = caseText.split(",");
		for (String pattern : patterns) {
			int start = pattern.indexOf('[');
			if(start == -1) {
				start = 0;
			}
			int end = pattern.indexOf(']', start);
			if(end == -1) {
				end = pattern.length();
			} else {
				end --;
			}
			String token = pattern.replace("[", "").replace("]", "");
			Token tk = Token.make(token, start, end);
			parts.put(token, tk);
		}
		tokenSet = parts.keySet();
		
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		tokenSet.forEach(token ->{
			int offset = word.indexOf(token);
			if(offset >= 0) {
				Token tk = parts.get(token);
				mk.addRange(Difficulty.L33, offset + tk.start, 2, offset + tk.end, -2);
			}
		});
		Levels.findAdjPos(word, prev, next, (range) -> {
			mk.addRange(Difficulty.L33, range[0], 2, range[1], -2);
		});
		return mk;
	}
	
	static class Token {
		String text;
		int start;
		int end;
		static Token make(String text, int start, int end) {
			Token tk =new Token();
			tk.text = text;
			tk.start = start;
			tk.end = end;
			return tk;
		}
	}
}
