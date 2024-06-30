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

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class TextDiff {
	
	public enum Cause {
		ADD,DEL,CHANGE,SAME
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
                result.add(Mapping.same(idx[0]++, offset[0]));
            }
            @Override
            public void visitInsertCommand(Character object) {
                cache.add(Mapping.add(idx[0], offset[0]));
                offset[0]++;
            }
            @Override
            public void visitDeleteCommand(Character object) {
            	Mapping prev =cache.isEmpty() ? null : cache.get(0);
            	if(prev != null && prev.cause == Cause.ADD) {
            		
        			result.add(Mapping.change(idx[0], prev.offset));
//        			cache.clear();
        			cache.remove(0);
        			decreaseOffset(cache);
        			offset[0]--;
        			idx[0]++;
            	} else {
            		Mapping diff = Mapping.deleted(idx[0], offset[0]);
            		result.add(diff);
            		offset[0]--;
            	}
            }
            private void decreaseOffset(List<Mapping> cache) {
        		for (Mapping mapping : cache) {
        			mapping.offset--;
        		}
        		
        	}
        });
        
        return new TextDiff(original, revised, result);
    }
	
	@ToString
	public static class Mapping {
		int charIdx;
		int offset;
		final Cause cause;
		public Mapping(int idx, int offset, Cause cause) {
			this.charIdx = idx;
			this.offset = offset;
			this.cause = cause;
			
		}
		static Mapping same(int idx, int offset) {
			return new Mapping(idx, offset, Cause.SAME);
		}
		static Mapping add(int idx, int offset) {
			return new Mapping(idx, offset, Cause.ADD);
		}
		static Mapping deleted(int idx, int offset) {
			return new Mapping(idx, offset, Cause.DEL);
		}
		static Mapping change(int idx, int offset) {
			return new Mapping(idx, offset, Cause.CHANGE);
		}
	}

}
