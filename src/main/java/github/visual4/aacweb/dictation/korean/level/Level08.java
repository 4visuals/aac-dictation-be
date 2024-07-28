package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.JamoSet;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 8. 끝에 오는 말 : ~ㅂ니다.
 * 
 * @author chminseo
 *
 */
public class Level08 implements ILevel {

	final Jamo pattern = Jamo.pattern("*", "*", "ㅂ");
	final JamoSet NIDA = JamoSet.decompose(pattern, "니다");
	final LevelContext ctx;
	
	public Level08(LevelContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		int [] pos = Levels.findSuffixPos(word, pattern, "니다");
		if (pos.length == 2) {
			mk.addRange(Difficulty.L08, pos[0] , pos[1]);
		}
		return mk;
	}
	
	public void eval(Mark mk, JamoSet sentence) {
		int idx = sentence.findIndex(NIDA, 0);
		int length = NIDA.getJamoLength();
		while(idx >= 0) {
			mk.markAndFlush(Difficulty.L08, idx, length);
			idx = sentence.findIndex(NIDA, idx + length);
		}
	}

}
