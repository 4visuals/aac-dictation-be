package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 26. 을과 를/은과 는 (앞글자에 받침이 있으면 을,은)(받침이 없으면 를,는)
 * 
 * FIXME 문맥을 파악해야 하는 규칙(조사인 경우)
 * 보은, 수은 -  
 * 
 * @author chminseo
 *
 */
public class Level26 implements ILevel {
	/**
	 * 는/를 - 앞글자 받침 없는 경우
	 */
	final Jamo jongsungN = Jamo.pattern("*", "*", "_");
	/**
	 * 은/을 - 앞글자 받침 있는 경우  
	 */
	final Jamo jongsungY = Jamo.pattern("*", "*", "ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ");
	final LevelContext ctx;
	
	Level26(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		String [] suffix = "는,를,은,을".split(",");
		for (int i = 0; i < suffix.length; i++) {
			Jamo pattern = i < 2 ? jongsungN: jongsungY; 
			int [] pos = Levels.findSuffixPos(word, pattern, suffix[i]);
			if (pos.length == 2) {
				mk.addRange(Difficulty.L26, pos[0] , pos[1]);
				break;
			}			
		}
		return mk;
	}
}
