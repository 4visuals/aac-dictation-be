package github.visual4.aacweb.dictation.domain.exam.dto;

import github.visual4.aacweb.dictation.domain.exam.ExamMode;
import github.visual4.aacweb.dictation.domain.sentence.Sentence.SentenceType;

public class PaperQuery {
	/**
	 * (required) fk to student.seq
	 */
	public Integer studentRef;
	public ExamMode mode;
	public SentenceType sentenceType;
	
	/**
	 * (optional) fk to section.seq
	 */
	public Integer sectionRef;
	/**
	 * 학생 별 학습정보 차트용 데이터
	 * @param studentSeq
	 * @return
	 */
	public static PaperQuery byDiagnosis(Integer studentSeq) {
		PaperQuery q = new PaperQuery();
		q.studentRef = studentSeq;
		q.mode = ExamMode.Q;
		q.sentenceType = null; //단어, 문장 전부 다
		q.sectionRef = null; // 모든 섹션 전부 다
		return q;
	}
}
