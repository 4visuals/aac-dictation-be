package github.visual4.aacweb.dictation.korean;
/**
 * 
 * <h4>초성 19개</h4> 
 *  <table style="border: 1px solid">
 *  <tr>
 *  <td>0</td><td>1</td><td>2</td><td>3</td><td>4</td><td>5</td><td>6</td><td>7</td><td>8</td><td>9</td><td>A</td><td>B</td><td>C</td><td>D</td><td>E</td><td>F</td><td>0</td><td>1</td><td>2</td></td>
 *  </tr>
 *  <tr>
 *  <td>ㄱ</td><td>ㄲ</td><td>ㄴ</td><td>ㄷ</td><td>ㄸ</td><td>ㄹ</td><td>ㅁ</td><td>ㅂ</td><td>ㅃ</td><td>ㅅ</td><td>ㅆ</td><td>ㅇ</td><td>ㅈ</td><td>ㅉ</td><td>ㅊ</td><td>ㅋ</td><td>ㅌ</td><td>ㅍ</td><td>ㅎ</td>
 *  </tr>
 *  </table>
 *  <h4>중성 21개</h4>
 *  <table style="border: 1px solid">
 *  <tr><td>0</td><td>1</td><td>2</td><td>3</td><td>4</td><td>5</td><td>6</td><td>7</td><td>8</td><td>9</td><td>A</td><td>B</td><td>C</td><td>D</td><td>E</td><td>F</td><td>0</td><td>1</td><td>2</td><td>3</td><td>4</td></tr>
 *  <tr><td>ㅏ</td><td>ㅐ</td><td>ㅑ</td><td>ㅒ</td><td>ㅓ</td><td>ㅔ</td><td>ㅕ</td><td>ㅖ</td><td>ㅗ</td><td>ㅘ</td><td>ㅙ</td><td>ㅚ</td><td>ㅛ</td><td>ㅜ</td><td>ㅝ</td><td>ㅞ</td><td>ㅟ</td><td>ㅠ</td><td>ㅡ</td><td>ㅢ</td><td>ㅣ</td></tr>
 *  </table>
 *  
 *  <h4>종성 29개</h4>
 *  <table  style="border: 1px solid">
 *  <tr><td>0</td><td>1</td><td>2</td><td>3</td><td>4</td><td>5</td><td>6</td><td>7</td><td>8</td><td>9</td><td>A</td><td>B</td><td>C</td><td>D</td><td>E</td><td>F</td><td>0</td><td>1</td><td>2</td><td>3</td><td>4</td><td>5</td><td>6</td><td>7</td><td>8</td><td>9</td><td>A</td><td>B</td></tr>
 *  <tr><td>_</td><td>ㄱ</td><td>ㄲ</td><td>ㄳ</td><td>ㄴ</td><td>ㄵ</td><td>ㄶ</td><td>ㄷ</td><td>ㄹ</td><td>ㄺ</td><td>ㄻ</td><td>ㄼ</td><td>ㄽ</td><td>ㄾ</td><td>ㄿ</td><td>ㅀ</td><td>ㅁ</td><td>ㅂ</td><td>ㅄ</td><td>ㅅ</td><td>ㅆ</td><td>ㅇ</td><td>ㅈ</td><td>ㅊ</td><td>ㅋ</td><td>ㅌ</td><td>ㅍ</td><td>ㅎ</td></tr>
 *  </table>
 *  * "_"은 받침없음을 나타냄
 *  
 *  <h4> sample '송'</h4>
 *  ㅅ(0x09) + ㅗ(0x08) + ㅇ(0x15)
 *  <ul>
 *  <li>ㅅ - 00000000 00000000 000000v0 00000000 | cho</li>
 *  <li>ㅗ - 00000000 00000000 0000000v 00000000 | jung</li>
 *  <li>ㅇ - 00000000 00v00000 00000000 00000000 | jong</li>
 *  </ul>
 *  
 * @author chminseo
 */
public class Jamo {
	// 음소 순서가 중요하므로 바꾸면 안됨.
	final private static String CHO = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ";
	final private static String JUNG = "ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ";
	final private static String JONG = "_ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ";
	
	/**
	 * 한글 자모 초성 시작 위치
	 */
	final private static int CHO_OFFSET = 0x1100;
	/**
	 * 한글 자모 중성 시작 위치
	 */
	final private static int JUNG_OFFSET = 0x1161;
	/**
	 * 한글 자모 종성 시작 위치
	 */
	final private static int JONG_OFFSET = 0x11A8;
	
	/**
	 * 한글 음절 시작 위치
	 */
	final static int KO_SYLLABLE_OFFSET = 0xAC00;
	
	/**
	 * "*'에 해당하는 초성 bit flag
	 */
	public final static int ANY_CHO_BIT = 524287;
	/**
	 * "*"에 해당하는 중성 bit flag
	 */
	public final static int ANY_JUNG_BIT= 2097151;
	/**
	 * "*"에 해당하는 종성 bit flag
	 */
	public final static int ANY_JONG_BIT = 0;
	
	int cho;
	int jung;
	int jong;
	
	Jamo(int cho, int jung, int jong) {
		this.cho = cho;
		this.jung = jung;
		this.jong = jong;
	}
	
	public static String decomposeKr(String origin) {
		
		char [] jamo = new char[origin.length() * 3];
		int pos = 0;
		for(int k = 0 ; k < origin.length(); k++ ) {
			int ko = origin.charAt(k);
			if(ko >= KO_SYLLABLE_OFFSET && ko < 0xD7B0) {
				int i0 = (ko - KO_SYLLABLE_OFFSET)/28/21;
				int i1 = (ko - KO_SYLLABLE_OFFSET)/28%21;
				int i2 = (ko - KO_SYLLABLE_OFFSET)%28;
				jamo[pos++] = CHO.charAt(i0);
				jamo[pos++] = JUNG.charAt(i1);
				jamo[pos++] = JONG.charAt(i2);	
			} else {
				jamo[pos++] = (char)ko;
				jamo[pos++] = (char)ko;
				jamo[pos++] = (char)ko;
			}
		}
		return new String(jamo, 0, pos);
	}
	
	public static Jamo decompose(int ko) {
		int i0 = (ko - KO_SYLLABLE_OFFSET)/28/21;
		int i1 = (ko - KO_SYLLABLE_OFFSET)/28%21;
		int i2 = (ko - KO_SYLLABLE_OFFSET)%28;
		return new Jamo(0x01 << i0, 0x01 << i1, 0x01 << i2);
	}
	private static int pos(String str, char ch) {
		int pos = str.indexOf(ch);
		if (pos < 0) {
			throw new RuntimeException(String.format("Jamo Error: jamo '%s' not found in [%s]\n", ch, str));
		}
		return pos;
	}
	public static Jamo pattern(String choes, String jungs, String jongs) {
		int cho = 0x00;
		if(choes.equals("*")) {
			choes = CHO;
		}
		if(jungs.equals("*")) {
			jungs = JUNG;
		}
		if(jongs.equals("*")) {
			jongs = JONG;
		}
		for(int i = 0 ; i < choes.length(); i++) {
			int offset = pos(CHO, choes.charAt(i)); 
			cho |= (0x01 << offset);
		}
		int jung = 0x00;
		for(int i = 0 ; i < jungs.length(); i++) {
			int offset = pos(JUNG, jungs.charAt(i));
			jung |= (0x01 << offset);
		}
		int jong = 0x00;
		for(int i = 0 ; i < jongs.length(); i++) {
			int offset = pos(JONG, jongs.charAt(i));
			jong |= (0x01 << offset);
		}
		return new Jamo(cho, jung, jong);
	}

	public boolean matched(char ch) {
		Jamo jm = Jamo.decompose(ch);
		return matched(jm);		
	}

	public boolean matched(Jamo jm) {		
		return (this.cho & jm.cho) > 0 
				&& (this.jung & jm.jung) > 0
				&& (this.jong & jm.jong) > 0;
	}

	public boolean hasJongsung() {
		return this.jong != 0x01;
	}
	private static char toJamo(int pattern, String target) {
		int cnt = 0;
		while(pattern > 1) {
			cnt ++;
			pattern >>= 1;
		}
		return target.charAt(cnt);
	}
	static char toChosung(int pattern) {
		return toJamo(pattern, CHO);
	}
	static char toJungsung(int pattern) {
		return toJamo(pattern, JUNG);
	}
	static char toJongsung(int pattern) {
		return toJamo(pattern, JONG);
	}
	
}
