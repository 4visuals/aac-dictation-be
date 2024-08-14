package github.visual4.aacweb.dictation.domain.assessment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.apache.commons.text.diff.CommandVisitor;
import org.apache.commons.text.diff.EditScript;
import org.apache.commons.text.diff.StringsComparator;

import github.visual4.aacweb.dictation.korean.Jamo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TextDiff {
	
	public enum Cause {
		/**
		 * 없는 단어가 들어감
		 */
		ADD,
		/**
		 * 있어야 할 글자가 사라짐(CHANGE의 특별한 경우로 치환홤)
		 */
		DEL,
		/**
		 * 단어가 바뀜
		 */
		CHANGE,
		/**
		 * 같음
		 */
		SAME
	}

	private String origin;
	private String revised;
	
	
	/**
	 * 문자열 revised의 각 글자마다 대응하는 원본 글자에 접근하기 위해 수정해야할 index 보정값을 나타냄
	 */
	private final List<Mapping> mappings;

	public TextDiff(String origin, String revised, List<Mapping> mappings) {
		this.origin = origin;
		this.revised = revised;
		this.mappings = mappings;
	}
	public String getReviseText(int start, int end) {
		int [] rev = this.transformPos(start, end);
//		int p = this.mappings.get(start).offset + start;
//		int q = this.mappings.get(end - 1).offset + end;
		return revised.substring(rev[0], rev[1]);
	}
	private int _mapIndex(int pos) {
		Mapping m = this.mappings.get(pos);
		return m.offset + m.charIdx;
	}
	public int[] transformPos(int start, int end) {
		int p = this._mapIndex(start);
		int q = this._mapIndex(end) + 1;
		return new int[] {p, q};
	}
	
	
	
	public static TextDiff build(String original, String revised) {
        List<Mapping> result = new ArrayList<>();
        List<Mapping> cache = new ArrayList<>();
        StringsComparator cmp = new StringsComparator(original, revised);
        EditScript<Character> script = cmp.getScript();
        
        final int[] offset = {0};
        final int[] idx = {0};

        script.visit(new CommandVisitor<Character>() {
            @Override
            public void visitKeepCommand(Character object) {
            	result.addAll(cache);
            	cache.clear();
                result.add(Mapping.same(idx[0]++, offset[0], object.toString()));
            }
            @Override
            public void visitInsertCommand(Character ch) {
                cache.add(Mapping.add(idx[0], offset[0], ch.toString()));
                offset[0]++;
            }
            @Override
            public void visitDeleteCommand(Character object) {
            	Mapping prev =cache.isEmpty() ? null : cache.get(0);
            	String correct = object.toString();
            	if(prev != null && prev.cause == Cause.ADD) {
            		
//        			result.add(Mapping.change(
//        					idx[0], prev.offset,
//        					correct,
//        					revised.substring(idx[0], idx[0] + 1)));
            		result.add(Mapping.change(
        					idx[0], prev.offset,
        					correct,
        					prev.wrong));
//        			cache.clear();
        			
        			decreaseOffset(cache);
        			offset[0]--;
            	} else {
            		Mapping diff = Mapping.deleted(idx[0], offset[0], correct);
            		result.add(diff);
            		offset[0]--;
            	}
            	idx[0]++;
            }
            private void decreaseOffset(List<Mapping> cache) {
        		for (Mapping mapping : cache) {
        			mapping.offset--;
        		}
        		
        	}
        });
//        int p = idx[0];
//        while(p < revised.length()) {
//        	result.add(Mapping.add(p, p + 1, revised.substring(p, p+1)));
//        	p++;
//        }
        return new TextDiff(original, revised, result);
    }
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Mapping m : mappings) {
			sb.append(String.format("(mod: %s, idx: %d, correct: %s, wrong: %s%n", m.cause, m.charIdx, m.correct, m.wrong));
		}
		return sb.toString();
	}
	@ToString
	public static class Mapping {
		int charIdx;
		int idx0;
		int idx1;
		int offset;
		final Cause cause;
		final String correct;
		final String wrong;
		final String correctJamo;
		final String wrongJamo;
		public Mapping(int idx, int offset, String correct, String wrong, Cause cause) {
			this.charIdx = idx;
			this.idx0 = 3 * idx;
			this.idx1 = this.idx0 + 3;
			this.offset = offset;
			this.correct = correct;
			this.wrong = wrong;
			this.cause = cause;
			this.correctJamo = Jamo.decomposeKr(this.correct);
			this.wrongJamo = Jamo.decomposeKr(this.wrong);
			
		}
		static Mapping same(int idx, int offset, String correct) {
			return new Mapping(idx, offset, correct, correct, Cause.SAME);
		}
		static Mapping add(int idx, int offset, String wrong) {
			return new Mapping(idx, offset,"", wrong, Cause.ADD);
		}
		static Mapping deleted(int idx, int offset, String correct) {
			return new Mapping(idx, offset, correct, "", Cause.DEL);
		}
		/**
		 * 
		 * @param idx
		 * @param offset
		 * @param falsed 틀린 글자
		 * @return
		 */
		static Mapping change(int idx, int offset, String correct, String wrong) {
			return new Mapping(idx, offset, correct, wrong, Cause.CHANGE);
		}
	}
	public String getJamoAt(int start, int end) {
//		int p = 0;
		int ch0 = start / 3;
		int ch1 = (end - 1) / 3;
		boolean singleWord = ch0 == ch1;
		StringBuilder sb = new StringBuilder();
		for (Mapping m : mappings) {
			int idx = m.idx0;
			if(idx >= end) {
				continue;
			}
			if(m.idx1 <= start) {
				continue;
			}
			if(singleWord && (m.cause == Cause.ADD || m.cause == Cause.DEL)) {
				continue;
			}
			for(int offset = 0 ; offset < 3 ; offset ++) {
				if(idx + offset >= start && idx + offset < end) {
					sb.append(m.wrongJamo.charAt(offset));
				}	
			}
		}
		return sb.toString();
	}

}
