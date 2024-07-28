package github.visual4.aacweb.dictation.korean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import github.visual4.aacweb.dictation.TypeMap;

public class Mark {

	CharSequence word;
	CharSequence jamo;
	Map<Difficulty, Pos> _posMap;	
	private List<Pos> marks;
	
	private Pos curPos;
	
	public Mark(CharSequence word) {
		this.word = word;
		this.jamo = Jamo.decomposeKr(word.toString());
		_posMap = new TreeMap<>();
		marks = new ArrayList<>();
	}
	
	public static Mark create(CharSequence word) {
		return new Mark(word);
	}
	/**
	 * 
	 * @param df
	 * @param charOffset 
	 * @return 
	 */
	public Pos mark(Difficulty df, int charOffset) {
		return mark(df, charOffset, 0);
	}
	/**
	 * 
	 * @param df
	 * @param charOffset - 글자 단위 offset
	 * @param offsetInChar - charOffset에서 중성 또는 종성을 가리키기 위한 offset. 0 | 1 | 2 중 하나
	 * @return
	 */
	public Pos mark(Difficulty df, int charOffset, int offsetInChar) {
		/*
		 * 글자 단위에서 자모음 단위로 변경하는 중
		 * 앞에 공백이 있어도 3글자로 처라된다.
		 */
		int jamoOffset = charOffset * 3;
		if (curPos == null) {
			initDf(df, jamoOffset + offsetInChar);
		} else {
			flushDf();
			initDf(df, jamoOffset + offsetInChar);
		}
		return curPos;
	}
	public void markAndFlush(Difficulty df, int start, int end) {
		Pos pos = initDf(df, start);
		pos.setEnd(end);
		flushDf();
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
	/**
	 * 
	 * @param df
	 * @param jamoOffset
	 * @return
	 */
	private Pos initDf(Difficulty df, int jamoOffset) {
		Pos existing = _posMap.get(df);
		if (existing == null) {
			existing = Pos.init(df, jamoOffset);
			addPos(existing);	
		} else {
			existing.setStart(jamoOffset);
			existing.setEnd(jamoOffset + 3);
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
		Pos pos = mark(df, start);
		pos.setEnd(3 * end);
		flushDf();
	}
//	public void addRange(Difficulty df, int start, int end, int offsetInChar) {
//		Pos pos = mark(df, start, offsetInChar);
//		pos.setEnd(3 * end);
//		flushDf();
//	}
	public void addRange(Difficulty df, int start, int offsetInStart,
			int end, int offsetInEnd) {
		Pos pos = mark(df, start, offsetInStart);
		pos.setEnd(3 * end + offsetInEnd);
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
			return new Pos(df, offset, offset + 3);
		}
		public int[] rangeAt(int index) {
			int [] r = this.ranges.get(index);
			return r;
		}
		public String toString() {
			StringBuilder sb = new StringBuilder();
			this.ranges.forEach(r -> {
				sb.append('[');
				sb.append(r[0]);
				sb.append(", ");
				sb.append(r[1]);
				sb.append(']');
			});
			return sb.toString();
		}
		public void each(Consumer<int[]> loop) {
			this.ranges.forEach(loop);
		}
	}

	public void eachLevel(BiConsumer<Difficulty, Pos> fn) {
		_posMap.forEach(fn);
	}

	public String textAt(int start, int end) {
		CharSequence seq = this.jamo.subSequence(start, end);
		return seq.toString();
	}
	
	public Map<String, Object> toMap() {
		TypeMap map = new TypeMap();
		this.eachLevel((df, pos) -> {
			map.put(df.name(), pos.ranges);
		});
		return map;
	}
	public List<Object> toList() {
		List<Object> arr = new ArrayList<>();
		this.eachLevel((df, pos) -> {
			arr.add(TypeMap.with("name", df.name(), "ranges", pos.ranges));
		});
		return arr;
	}

}
