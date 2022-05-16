package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 29. ~ㅕ요,~였다. ~였어요. ~였습니다. 
 * ('ㅣ다.'동사 원형의 어미는 ㅕ가 됨)
 * (예:가지다→가져요.) 
 * 
 * ~ㅕ요 => 벗겨요(벗기다). 디뎌요(디디다). 속여요(속이다). 마셔요(마시다).
 * ~ㅕㅆ다,
 * ~ㅕㅆ어요.
 * ~ㅕㅆ습니다. 
 *  
 * @author chminseo
 *
 */
public class Level29 implements ILevel {

	final Jamo pattern1 = Jamo.pattern("*", "ㅕ", "_"); // + '요'
	final Jamo pattern2 = Jamo.pattern("*", "ㅕ", "ㅆ");// + '다', '어요', '습니다'
	final LevelContext ctx;
	
	Level29(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		String [] suffix = "요,다,어요,습니다".split(",");
		for (int i = 0; i < suffix.length; i++) {
			Jamo pattern = i == 0 ? pattern1 : pattern2;
			int [] pos = Levels.findSuffixPos(word, pattern, suffix[i]);
			if (pos.length == 2) {
				mk.addRange(Difficulty.L29, pos[0] , pos[1]);
				break;
			}
		}
		return mk;
	}
}
