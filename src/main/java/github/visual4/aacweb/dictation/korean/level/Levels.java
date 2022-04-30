package github.visual4.aacweb.dictation.korean.level;

import java.util.function.Consumer;

import github.visual4.aacweb.dictation.korean.Jamo;

public class Levels {

	private final static int [] NONE = new int[0];
	static int [] findSuffixPos(String word, Jamo pattern, String suffix) {
		
		int end = word.endsWith(".") ? word.length() - 1 : word.length(); 
		int start = end - suffix.length();
		if (start < 1) {
			// suffix 앞에 한글자 더 있어야 함 
			return NONE;
		}
		if (!word.substring(start, end).equals(suffix)) {
			// suffix 로 끝나야 함
			return NONE;
		}
		// suffix 앞의 한 글자가 pattern으로 끝나는지 확인
		char ch = word.charAt(start - 1);
		if (pattern.matched(ch)) {
			return new int[] {start - 1, end};
		} else {
			return NONE;
		}
	}
	static void findAdjPos(String word, Jamo prev, Jamo next, Consumer<int[]> fn) {
		if (word.length() < 2) {
			return;
		}
		for(int k = 0; k < word.length() - 1; k++) {
			char c0 = word.charAt(k+0);
			char c1 = word.charAt(k+1);
			if (prev.matched(c0) && next.matched(c1)) {
				fn.accept(new int[] {k, k+2});
			}
		}
	}
}
