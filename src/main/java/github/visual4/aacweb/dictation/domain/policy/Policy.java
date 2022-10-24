package github.visual4.aacweb.dictation.domain.policy;

import java.time.Instant;
import java.time.LocalDate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Policy {

	public enum PolicyType {
		TM,
		PV
	}
	@EqualsAndHashCode.Include
	Integer seq;
	
	PolicyType policyType;
	Instant creationTime;
	String detail;
	LocalDate activatedAt;
	
}
