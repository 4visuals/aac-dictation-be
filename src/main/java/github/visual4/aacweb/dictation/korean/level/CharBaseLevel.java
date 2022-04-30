package github.visual4.aacweb.dictation.korean.level;

import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;

public class CharBaseLevel implements ILevel {

	final LevelContext ctx;
	final Jamo pattern;
	
	public CharBaseLevel(LevelContext ctx, Jamo pattern) {
		this.pattern = pattern;
		this.ctx = ctx;
	}

	@Override
	public Mark eval(String word) {
		// TODO Auto-generated method stub
		return null;
	}

}
