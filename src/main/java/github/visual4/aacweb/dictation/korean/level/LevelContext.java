package github.visual4.aacweb.dictation.korean.level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.Util;
import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;

public class LevelContext {

	private Map<CharSequence, Mark> marks ;
	
	private List<ILevel> levels;
	public LevelContext(MarkFactory markFactory) {
		this.marks = new HashMap<>();
		this.levels = new ArrayList<>();
		install();
	}
	public Mark findMark(CharSequence word) {
		Mark mk = marks.get(word);
		if (mk == null) {
			mk  = Mark.create(word);
			marks.put(word, mk);
		}
		return mk;
	}
	
	public static LevelContext createContext() {
		return new LevelContext(new MarkFactory());
	}
	public Mark setMark(Difficulty df, Jamo pattern, CharSequence word) {
		Mark m = findMark(word);
		for(int k = 0; k < word.length(); k++) {
			char ch = word.charAt(k);
			if (pattern.matched(ch)) {
				m.mark(df, k);
			} else {
				m.reset();
			}
		}
		m.reset();
		return m;
	}
	
	public Mark parseDifficulties(String sentence) {
		Mark mark = null;
		for (ILevel lvl : levels) {
			mark = lvl.eval(sentence);
		}
		return mark;
	}
	private void install() {
		Object [] args = new Object[] {this};
		StringBuilder fqName = new StringBuilder("github.visual4.aacweb.dictation.korean.level.Level");
		int offset = fqName.length();
		// FIXME 41, 44, 46 미구현 상태
		for(int lvl = 1 ; lvl <= 44 ; lvl++) {
			fqName.append(lvl);
			try {
				Class<?> cls = Class.forName(fqName.toString());
				ILevel instance = Util.createInstance(cls, args, LevelContext.class);
				levels.add(instance);
			} catch (ClassNotFoundException e) {
				throw new AppException(ErrorCode.SERVER_ERROR, 500, "fail to load Level: " + fqName.toString());
			} finally {
				fqName.delete(offset, fqName.length());
			}
		}
	}
}
