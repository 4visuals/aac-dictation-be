package github.visual4.aacweb.dictation.domain.stats;

import java.time.Instant;

import github.visual4.aacweb.dictation.domain.sentence.Sentence.SentenceType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SectionStats {
	
	Integer examRef;   // FK to ExamPaper.seq
	Integer sectionRef;
	Integer studentRef;
	
	Instant startTime;
	
	Integer total;
	Integer correct;
	Integer failed;
	
	SentenceType type;
}
