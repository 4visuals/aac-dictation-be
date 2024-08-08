package github.visual4.aacweb.dictation.service.analysis;

import java.util.Arrays;

public class Tagging {

	public int [] pos = new int[2];
	public String pumsa;
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Tagging [pos=").append(Arrays.toString(pos)).append(", pumsa=").append(pumsa).append("]");
		return builder.toString();
	}
	
	
	
}
