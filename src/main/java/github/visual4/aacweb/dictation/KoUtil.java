package github.visual4.aacweb.dictation;

import java.util.Set;

public class KoUtil {
	/**
	 * 초성 19, 중성 21, 종성 29
	 */
	private static String [] CHO = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ".split("");
	private static String [] JUNG = "ㅏㅐㅑㅒㅓㅔㅓㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ".split("");
	private static String [] JONG = "_ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ".split("");
	/**
	 * 한글 자모 초성 시작 위치
	 */
	private static int CHO_OFFSET = 0x1100;
	/**
	 * 한글 자모 중성 시작 위치
	 */
	private static int JUNG_OFFSET = 0x1161;
	/**
	 * 한글 자모 종성 시작 위치
	 */
	private static int JONG_OFFSET = 0x11A8;
	
	/**
	 * 한글 음절 시작 위치
	 */
	private static final int KO_SYLLABLE_OFFSET = 0xAC00;

	public static String [] decompose(int ko) {
		System.out.printf("%d, %d, %d\n", CHO.length, JUNG.length, JONG.length);
		// 삯 쬻
		int idx0 = (ko - KO_SYLLABLE_OFFSET)/28/21;
		int idx1 = (ko - KO_SYLLABLE_OFFSET)/28%21;
		int idx2 = (ko - KO_SYLLABLE_OFFSET)%28;
		return new String [] {CHO[idx0], JUNG[idx1], JONG[idx2].trim()};
	}
	public static String [] toCodes(int ko) {
		int idx0 = (ko - KO_SYLLABLE_OFFSET)/28/21;
		int idx1 = (ko - KO_SYLLABLE_OFFSET)/28%21;
		int idx2 = (ko - KO_SYLLABLE_OFFSET)%28;
		return new String [] {h(idx0+CHO_OFFSET), h(idx1 + JUNG_OFFSET), h(idx2 + JONG_OFFSET - 1)};
	}
	
	private static String h(int base10) {
		return Integer.toHexString(base10);
	}
	
	public static char compose(int [] codes) {
		return (char)( ( (codes[0]-CHO_OFFSET) * 21  + (codes[1]-JUNG_OFFSET)) * 28 + codes[2]- JONG_OFFSET +1 + KO_SYLLABLE_OFFSET);
	}
}
