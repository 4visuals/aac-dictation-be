package github.visual4.aacweb.dictation.korean.level;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import github.visual4.aacweb.dictation.korean.Difficulty;

public class SampleReader {

	static Map<Difficulty, List<String>> samples;
	
	private static void install(String fname, String delim) {
		InputStream in = SampleReader.class.getResourceAsStream(fname);
		Scanner sc = new Scanner(in);
		samples = new HashMap<>();
		
		List<String> current = null;
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			if (line.startsWith("#")) {
				line = line.substring(1);
				samples.put(Difficulty.valueOf(line), current = new ArrayList<>());
			} else {
				current.add(line.trim());
//				String [] words = line.trim().split(delim);
//				for (String word : words) {
//					current.add(word);
//				}
			}
		}	
	}
	public static List<String> getByLevel(int level, String fname, String delim) {
		String name = "Level" + level;
		Difficulty lvl = Difficulty.valueOf(name);
		return get(lvl, fname, delim);
	}
	public static List<String> get(Difficulty difficuty, String fname, String delim) {
		if (samples == null) {
			install(fname, delim);
		}
		return samples.get(difficuty);
	}
}
