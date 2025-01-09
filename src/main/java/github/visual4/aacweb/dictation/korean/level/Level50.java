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
	String pattern ="가까이,가만히,감사히,같이,고요히,고이,곰곰이,괜히,굉장히,급히,깨끗이,꼼꼼히,나날이,낱낱이,높이,다소곳이,도저히,많이,반듯이,버젓이,부지런히,분명히,샅샅이,새로이,솔직히,쓸쓸히,안녕히,열심히,우연히,자세히,정확히,조용히,줄줄이,즐거이,천천히,특히,틈틈이";
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
