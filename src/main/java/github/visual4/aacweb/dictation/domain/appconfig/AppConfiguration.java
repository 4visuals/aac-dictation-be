package github.visual4.aacweb.dictation.domain.appconfig;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import github.visual4.aacweb.dictation.AppStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppConfiguration {

	Long adminAccountSeq;
	/**
	 * 계정당 무료 수강권 갯수
	 * 
	 * 계정 생성할때 주어진 갯수만큼의 수강권을 생성함
	 */
	Integer freeCertsPerUser;
	/**
	 * 결제 유예기간(시간단위)
	 * 
	 * 수강권 구매 후 결제하기까지 유예 시간.주어진 시간동안은 결제없이 기능을 사용할 수 있다. 
	 */
	long deferredPaymentDurationPerHour;
	
	List<Long> adminAccounts;
	
	AppStatus appStatus;
	/**
	 * 주어진 시간으로부터 결제 유예기간 반환
	 * @param baseTime
	 * @return
	 */
	public Instant getPaymentDueTime(Instant baseTime) {
		Instant dueTime = baseTime.plus(deferredPaymentDurationPerHour, ChronoUnit.HOURS);
		return dueTime;
	}
	public boolean isAdmin(Long userSeq) {
		return this.adminAccounts.contains(userSeq);
	}
}
