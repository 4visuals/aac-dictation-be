package github.visual4.aacweb.dictation.korean.level;

import java.util.Arrays;
import java.util.List;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Mark;

public class Reporting {

	static class Str {
		char [] chr ;
		final String src;
		Str(String src) {
			this.src = src;
			chr = new char[src.length()];
			Arrays.fill(chr, '_');
		}
		void expose(int[] range) {
			for(int offset = range[0]; offset < range[1]; offset++) {
				chr[offset] = src.charAt(offset);
			}
			
		}
		@Override
		public String toString() {
			return new String(chr);
		}
	}
	public static void main(String[] args) {
		Difficulty[] dfs = Difficulty.values();
		for(int k = 0 ; k < dfs.length; k++) {
			Difficulty df = dfs[k];
			if (df.level == 0 || (df.level >= 43 &&  df.level <= 45) || df.level >= 49) {
				System.out.println("### " + df.desc);
				System.out.println("* 생략(구현 안됨)");
				System.out.println();
				continue;
			}
			print(df);
		}
	}
	static void print(Difficulty dff) {
		List<String> sentences = SampleReader.get(dff, "sen.txt", "\t");
		LevelContext ctx = new LevelContext();
		System.out.println("### " + dff.desc);
		for (String sen : sentences) {
			Mark mark = ctx.parseDifficulties(sen);
			System.out.printf("#### [LEVEL%d] %s\n",dff.level, sen.trim());
			System.out.println("```");
			mark.each((df, pos) -> {
				boolean overflow = df.level > dff.level;
				Str s = new Str(sen);
				System.out.printf("%s %02d: ", (overflow ? "+" : " "), df.level);
				for(int k = 0 ; k < pos.size(); k++) {
					// System.out.printf("%2d: %s\n", df.level, Arrays.toString(pos.rangeAt(k)));
					s.expose(pos.rangeAt(k));
				}
				System.out.println(s);
			});
			System.out.println("```");
		}
	}
}
