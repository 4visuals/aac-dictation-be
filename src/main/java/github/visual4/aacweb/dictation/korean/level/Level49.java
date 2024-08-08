package github.visual4.aacweb.dictation.korean.level;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import github.visual4.aacweb.dictation.korean.Difficulty;
import github.visual4.aacweb.dictation.korean.Jamo;
import github.visual4.aacweb.dictation.korean.Mark;
/**
 * 49. 사이시옷
 * @author chminseo
 *
 */
public class Level49 implements ILevel {
	final List<String> matching = Arrays.asList("갯벌,고깃덩이,곳간,귓속,깃발,깻잎,나뭇가지,나뭇잎,나뭇진,냇가,냇물,뒷모습,뒷일,맷돌,바닷가,바닷물,밧줄,뱃놀이,볏단,빗방울,숫자,아랫니,어젯밤,옛날,옷걸이,전봇대,젓가락,조갯살,존댓말,찻길,찻잔,칫솔,콧구멍,콧노래,콧물,콧속,텃밭,햇볕,햇빛,햇살,헛간,혼잣말,횃불".split(","));
//	final Jamo prev = Jamo.pattern("*", "*", "ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ");
//	final Jamo next = Jamo.pattern("ㅇ", "ㅣㅑㅕㅛㅠ", "*");
	final LevelContext ctx;
	
	Level49(LevelContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public Mark eval(String word) {
		Mark mk = ctx.findMark(word);
		for (String matched : matching) {
			int idx = word.indexOf(matched);
			if(idx >= 0) {
				mk.addRange(Difficulty.L49, idx, idx + matched.length());
			}
		}
		return mk;
	}
}
