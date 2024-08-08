package github.visual4.aacweb.dictation.korean.level;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
import github.visual4.aacweb.dictation.service.analysis.SentenceTag;
import github.visual4.aacweb.dictation.service.analysis.Tagging;
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
	
	private static String EUN = "JX"; // ~은 ~는
	private static String EUL = "JKO"; // ~을 ~를
	private static String WA1 = "JC"; // ~와, ~과 접속조사
	private static String WA2 = "JKB"; // 부사격 조사 (총알과 바꾼)
	
	// 과 : JKB
	
	private Set<String> pumsaSet = Set.of("JX", "JKO", "JC", "JKB");
	private Map<String, Set<String>> pumsaMap = new HashMap<>();
	final LevelContext ctx;
	
	Level28(LevelContext ctx) {
		this.ctx = ctx;
		this.pumsaMap.put("JX", Set.of("은", "는"));
		this.pumsaMap.put("JKO", Set.of("을", "를"));
		this.pumsaMap.put("JC", Set.of("와", "과"));
		this.pumsaMap.put("JKB", Set.of("와", "과"));
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		Map<String, SentenceTag> taging = ctx.getTagging();
		SentenceTag sen = taging.get(word);
		if (sen != null) {
			for (Tagging tag : sen.tags) {
				Set<String> tokens = pumsaMap.get(tag.pumsa);
				if(tokens != null) {
					String token = word.substring(tag.pos[0], tag.pos[1]);
					if (tokens.contains(token)) {
						mk.addRange(Difficulty.L28, tag.pos[0], tag.pos[1]);
					}
				}
			}
		}
		return mk;
	}
}
