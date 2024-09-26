package github.visual4.aacweb.dictation.domain.exam;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

import github.visual4.aacweb.dictation.domain.sentence.Sentence.SentenceType;
import github.visual4.aacweb.dictation.tools.Origin;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
/**
 * 시험에 대한 제출 답안
 * @author chminseo
 *
 */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ExamPaper {
	@EqualsAndHashCode.Include
	Long seq;
	/**
	 * fe에서 첨부하는 라이선스 uuid. 연결된 학생 계정을 찾을때 사용함
	 * @transient 
	 */
	String license;
	
	Integer sectionRef;
	Origin origin;
	
	Long studentRef; // Fk to User.seq
	Integer ageInMonth;
	
	SentenceType type;
	ExamMode mode;
	Instant startTime;
	Instant endTime;
	Integer questionOffset;
	/**
	 * 문항 수
	 */
	Integer numOfQuestions;
	/**
	 * 오답연습 및 재시도 시험인지 나타냄. true인 경우 현재 section에 대한 오답의 갯수만 업데이트 해야함. 
	 */
	boolean retry;
	/**
	 * 학습이력에서 참조할 Level별 상세 오답 정보가 분석되었는지 나타냄
	 */
	Boolean analyzed;
	
	List<ExamAnswer> submissions;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("licesnse: ");
		sb.append(license);
		sb.append(", SEQ: ");
		sb.append(seq);
		sb.append(", sectionRef: ");
		sb.append(sectionRef);
		sb.append(", (");
		sb.append(origin);
		sb.append(")");
		sb.append(", offset: ");
		sb.append(questionOffset);
		sb.append(", start: ");
		sb.append(startTime);
		sb.append(", student: ");
		sb.append(studentRef);
		sb.append(", type: ");
		sb.append(type);
		sb.append(", mode: ");
		sb.append(mode);
		sb.append('\n');
		for (int i = 0; i < submissions.size(); i++) {
			sb.append('>');
			sb.append(' ');
			sb.append(submissions.get(i));
			sb.append('\n');
		}
		return sb.toString();
	}

	public void eachAnswer(Consumer<ExamAnswer> consumer) {
		for (ExamAnswer examAnswer : submissions) {
			consumer.accept(examAnswer);
		}
	}

	public String getYMD() {
		/*
		 * FIXME 서버의 시간대가 UTC 이면 9시간을 더하지 못한다.
		 *  
		 */
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
	            .withZone(ZoneId.systemDefault());
		return formatter.format(this.startTime);
	}

	public boolean checkIfAnalyzied() {
		return this.analyzed != null && this.analyzed.booleanValue();
	}
	
}
