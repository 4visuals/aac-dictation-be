package github.visual4.aacweb.dictation.korean.level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.Util;
import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.JamoSet;
import github.visual4.aacweb.dictation.korean.Mark;

public class LevelContext {

	private Map<CharSequence, Mark> marks ;
	
	private List<ILevel> levels;
	public LevelContext() {
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
		return new LevelContext();
	}
	public Mark setMark(Difficulty df, Jamo pattern, CharSequence word) {
		return setMark(df, pattern, word, null);
	}
	public Mark setMark(Difficulty df, Jamo pattern, CharSequence word, Consumer<Mark> setter) {
		Mark m = findMark(word);
		for(int k = 0; k < word.length(); k++) {
			char ch = word.charAt(k);
			if (pattern.matched(ch)) {
				if(setter != null) {
					setter.accept(m);
				} else {
					m.mark(df, k);
				}
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
		/*
		 * FIXME 모든 LEVEL 인스턴스에 연결된 ctx 변수를 제거하면 아래와 같이 50개의 level 인스턴스를 만들 필요가 없음
		 */
		Object [] args = new Object[] {this};
		StringBuilder fqName = new StringBuilder("github.visual4.aacweb.dictation.korean.level.Level");
		int offset = fqName.length();
		for(int lvl = 1 ; lvl <= 50 ; lvl++) {
			if (lvl < 10) {
				fqName.append('0');
			}
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
	public Mark mark(Level08 lvl8, JamoSet set) {
		Mark mk = findMark(set.word);
		lvl8.eval(mk, set);
		return mk;
	}
}
