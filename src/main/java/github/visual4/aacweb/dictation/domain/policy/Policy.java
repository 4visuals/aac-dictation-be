package github.visual4.aacweb.dictation.domain.policy;

import java.time.Instant;
import java.time.LocalDate;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
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
	public void assertNew() {
		if (this.seq != null) {
			throw new AppException(ErrorCode.INVALID_VALUE, 400);
		}
		if(detail == null) {
			throw new AppException(ErrorCode.INVALID_VALUE, 400);
		}
		if (activatedAt == null) {
			throw new AppException(ErrorCode.INVALID_VALUE, 400);
		}
		if (policyType == null) {
			throw new AppException(ErrorCode.INVALID_VALUE, 400);
		}
	}
	
}
