package github.visual4.aacweb.dictation.korean.level;

import java.util.Arrays;
import java.util.List;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 50. '이'와 '히'를 구분해요
 * FIXME 구현 안됨
 * @author chminseo
 *
 */
public class Level50 implements ILevel {
	String pattern ="깨끗이,반듯이,같이,가까이,줄줄이,급히,특히,정확히,조용히,열심히,많이,고이,가만히,꼼꼼히,도저히,틈틈이,고요히,나날이,새로이,쓸쓸히,천천히,우연히,즐거이,높이,곰곰이,자세히,버젓이,솔직히,다소곳이,분명히";
	List<String> words = Arrays.asList(pattern.split(","));
	
	final Jamo prev = Jamo.pattern("*", "*", "ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ");
	final Jamo next = Jamo.pattern("ㅇ", "ㅣㅑㅕㅛㅠ", "*");
	final LevelContext ctx;
	
	Level50(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		Levels.inject(mk, word, words, Difficulty.L50);
		return mk;
	}
}
