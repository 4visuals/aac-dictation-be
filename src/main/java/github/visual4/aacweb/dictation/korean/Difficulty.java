package github.visual4.aacweb.dictation.korean;
/**
 * 어절의 난이도 모음
 * @author chminseo
 *
 */
public enum Difficulty {

	NONE(0, "해당없음"),
	L1(1, "1. 자음(ㅇㄱㄴㄷㄹㅁㅂㅅㅈ) + 모음(ㅏㅓㅗㅜㅡㅣ) + 받침X"),
	L2(2, "2. 자음(ㅇㄱㄴㄷㄹㅁㅂㅅㅈ) + 모음(ㅑㅕㅛㅠ) + 받침X"),
	L3(3, "3. 자음(ㅊㅋㅌㅍㅎ) + 모든 모음 + 받침X");
	
	final int level;
	final String desc;
	private Difficulty(int lvl, String desc) {
		this.level = lvl;
		this.desc = desc;
	}
}
