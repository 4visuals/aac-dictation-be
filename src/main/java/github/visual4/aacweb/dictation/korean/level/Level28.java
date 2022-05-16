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
public class Level28 implements ILevel {
	/**
	 * 는/를 - 앞글자 받침 없는 경우
	 */
	final Jamo jongsungN = Jamo.pattern("*", "*", "_");
	/**
	 * 은/을 - 앞글자 받침 있는 경우  
	 */
	final Jamo jongsungY = Jamo.pattern("*", "*", "ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ");
	final LevelContext ctx;
	
	Level28(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		String [] words = word.split(" ");
		Mark mk = ctx.findMark(word);
		String [] suffix = "와,는,를,과,은,을".split(",");
		for (int i = 0; i < suffix.length; i++) {
			Jamo pattern = i < 3 ? jongsungN: jongsungY;
			int offset = 0;
			for (int k = 0; k < words.length; k++) {
				int [] pos = Levels.findSuffixPos(words[k], pattern, suffix[i]);
				if (pos.length == 2) {
					mk.addRange(Difficulty.L28, offset + pos[0]+1 , offset + pos[1]);
				}
				offset += words[k].length() + 1;
			}
		}
		return mk;
	}
}
