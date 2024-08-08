package github.visual4.aacweb.dictation.korean.level;

import java.util.Iterator;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 38. 소리를 닮아가요(2)
 * (ㄴ+ㄹ→ㄹ,ㅁㅇ+ㄹ→ㄴ)
 * 
 * 제5장 음의 동화
 * 19항 - 받침 ‘ㅁ, ㅇ’ 뒤에 연결되는 ‘ㄹ’은 [ㄴ]으로 발음한다.
 * 20항 - ‘ㄴ’은 ‘ㄹ’의 앞이나 뒤에서 [ㄹ]로 발음한다.
 * https://kornorms.korean.go.kr/regltn/regltnView.do?regltn_code=0002&regltn_no=346#a412
 * @author chminseo
 *
 */
public class Level38 implements ILevel {
	// 영리, 승리, 
	final Jamo [] _5j_19h = {Jamo.pattern("*", "*", "ㅁㅇ"), Jamo.pattern("ㄹ", "*", "*")};
	final Jamo [] _5j_20h_1 = {Jamo.pattern("*", "*", "ㄴ"), Jamo.pattern("ㄹ", "*", "*")};
	final Jamo [] _5j_20h_2 = {Jamo.pattern("*", "*", "ㄹ"), Jamo.pattern("ㄴ", "*", "*")};
	
	final Jamo [][] patterns = {_5j_19h, _5j_20h_1, _5j_20h_2};
	
	
	final LevelContext ctx;
	
	Level38(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		// 물난리 => 물랄리
		Mark mk = ctx.findMark(word);
		for (int k = 0; k < patterns.length; k++) {
			Jamo [] ptn = patterns[k];
			Levels.findAdjPos(word, ptn[0], ptn[1], (range) -> {
				mk.addRange(Difficulty.L38, range[0], 2, range[1], -2);
			});
			
		}
		return mk;
	}
}
