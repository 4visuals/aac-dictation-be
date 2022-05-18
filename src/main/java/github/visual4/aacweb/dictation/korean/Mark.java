package github.visual4.aacweb.dictation.korean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;

import github.visual4.aacweb.dictation.TypeMap;

public class Mark {

	CharSequence word;
	Map<Difficulty, Pos> _posMap;	
	private List<Pos> marks;
	
	private Pos curPos;
	
	public Mark(CharSequence word) {
		this.word = word;
		_posMap = new TreeMap<>();
		marks = new ArrayList<>();
	}
	
	public static Mark create(CharSequence word) {
		return new Mark(word);
	}
	/**
	 * 
	 * @param df
	 * @param pos - (exclusive)
	 */
	public void mark(Difficulty df, int pos) {
		if (curPos == null) {
			initDf(df, pos);
		} else if (curPos.df == df){
			curPos.setEnd(pos + 1);
		} else if (curPos.df != df) {
			flushDf();
			initDf(df, pos);
		}
	}
	
	public void reset() {
		flushDf();
	}
	
	private void flushDf() {
		if (curPos != null) {
			curPos.flush();
			curPos = null;			
		}
	}
	private Pos initDf(Difficulty df, int pos) {
		Pos existing = _posMap.get(df);
		if (existing == null) {
			existing = Pos.init(df, pos);
			addPos(existing);	
		} else {
			existing.setStart(pos);
			existing.setEnd(pos + 1);
		}
		
		return curPos = existing;
		
	}
	
	private void addPos(Pos pos) {
		_posMap.put(pos.df, pos);
		marks.add(pos);
	}
	public boolean has(Difficulty df) {
		return marks.stream().anyMatch(mk -> mk.df == df);
	}

	public Pos pos(Difficulty df) {
		return _posMap.get(df);
	}

	public void addRange(Difficulty df, int start, int end) {
		mark(df, start);
		curPos.setEnd(end);
		flushDf();
	}

	public static class Pos {
		final Difficulty df;
		int start;
		int end;
		List<int[]> ranges;
		private Pos(Difficulty df, int start, int end) {
			this.df = df;
			this.start = start;
			this.end = end;
			this.ranges = new ArrayList<>(2);
		}
		void setStart(int start) {
			this.start = start;
		}
		public void flush() {
			this.ranges.add(new int[] {start, end});
		}
		void setEnd(int end) {
			this.end = end;
		}
		public int size() {
			return this.ranges.size();
		}
		public int [] arr() {
			return this.ranges.get(0);
		}
		static Pos init(Difficulty df, int offset) {	
			return new Pos(df, offset, offset + 1);
		}
		public int[] rangeAt(int index) {
			int [] r = this.ranges.get(index);
			return r;
		}
	}

	public void each(BiConsumer<Difficulty, Pos> fn) {
		_posMap.forEach(fn);
		
	}

	public Map<String, Object> toMap() {
		TypeMap map = new TypeMap();
		this.each((df, pos) -> {
			map.put(df.name(), pos.ranges);
		});
		return map;
	}
	public List<Object> toList() {
		List<Object> arr = new ArrayList<>();
		this.each((df, pos) -> {
			arr.add(TypeMap.with("name", df.name(), "ranges", pos.ranges));
		});
		return arr;
	}

}
