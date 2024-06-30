package github.visual4.aacweb.dictation.korean;

public class JamoSet {

	public final int[] patterns;
	public final String word;

	public JamoSet(int[] patterns, String word) {
		this.patterns = patterns;
		this.word = word;
	}
	
	public int getCharLength() {
		return this.patterns.length /3;
	}

	public Integer getJamoLength() {
		return this.patterns.length;
	}
	private static JamoSet decompose(int [] patterns, int offset, String word) {
		char [] chr = word.toCharArray();
		for (int k = 0; k < chr.length; k++) {
			int i0 = (chr[k] - Jamo.KO_SYLLABLE_OFFSET)/28/21;
			int i1 = (chr[k] - Jamo.KO_SYLLABLE_OFFSET)/28%21;
			int i2 = (chr[k] - Jamo.KO_SYLLABLE_OFFSET)%28;
			patterns[3 * (k + offset)] = 0x01 << i0;
			patterns[3 * (k + offset) +1] = 0x01 << i1;
			patterns[3 * (k + offset) +2] = 0x01 << i2;
		}
		return new JamoSet(patterns, word);
	}
	/**
	 * 
	 * @param word
	 * @param offset (0 | 1 | 2) word의 첫 글자가 초성, 중성, 종성인지 나타냄. 0이면 초성부터, 1이면 (중성,종성)으로 해석, 2이면 종성으로 mapping  
	 * @return
	 */
	public static JamoSet decompose(Jamo partial, String word) {
		int size = 3 * word.length() + 3;
		int [] patterns = new int[size];
		patterns[0] = partial.cho;
		patterns[1] = partial.jung;
		patterns[2] = partial.jong;
		return decompose(patterns, 1, word);
	}
	public static JamoSet decompose(String word) {
		return decompose(new int[word.length() * 3],0, word);
	}

	public char[] getJamo() {
		char [] jamo = new char[this.getJamoLength()];
		for (int k = 0; k < patterns.length; k+=3) {
			jamo[k + 0] = Jamo.toChosung(patterns[k]);
			jamo[k + 1] = Jamo.toJungsung(patterns[k + 1]);
			jamo[k + 2] = Jamo.toJongsung(patterns[k + 2]);
		}
		return jamo;
	}
	private int matchedAt (int offset, JamoSet other) {
		int len = other.getJamoLength();
		if(offset + len > this.getJamoLength()) {
			return -1;
		}
		int pos = 0;
		for(int k = 0 ; k < len ; k+=3) {
			int p = this.patterns[offset + k];
			int q = other.patterns[k]; 
			if(q == Jamo.ANY_CHO_BIT) {
				pos ++;
			} else if ( (p & q) == 0) {
				return -1;
			}
			p = this.patterns[offset + k + 1];
			q = other.patterns[k + 1];
			if(q == Jamo.ANY_JUNG_BIT) {
				pos++;
			} else if ((p & q) == 0) {
				return -1;
			}
			p = this.patterns[offset + k + 2];
			q = other.patterns[k + 2];
			if (( p & q) == 0) {
				return -1;
			}
		}
		return pos;
		
	}

	public int findIndex(JamoSet other, int offset) {
		int k = offset; 
		while(k < patterns.length) {
			int pos = matchedAt(k, other);
			if(pos >= 0) {
				return k + pos;
			} else {
				k+=3;
			}
		}
		return -1;
	}
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int k = 0; k < patterns.length; k+=3) {
			sb.append(Jamo.toChosung(patterns[k]));
			sb.append(Jamo.toJungsung(patterns[k+1]));
			sb.append(Jamo.toJongsung(patterns[k+2]));
		}
		return sb.toString();
	}
}
