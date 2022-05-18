package github.visual4.aacweb.dictation.domain.license;

import java.time.Instant;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class License {
	public enum Column {
		lcs_seq,
		receiver_ref,
		lcs_uuid
	}

	@EqualsAndHashCode.Include
	Long seq;
	
	String uuid;
	/**
	 * 주문에 대한 참조.
	 * 계정마다 생성되는 무료 라이선스(2개)는 이 값이 null이다.
	 * 값이 있다면 dt_orders.odr_seq 에 대한 FK
	 */
	Integer orderRef; // FK to dt_orders
	/**
	 * 라이선스가 적용된 학생의 seq
	 * 사용 전이면 null.
	 * 값이 있다면 dt_users.user_seq에 대한 FK
	 */
	Long studentRef;
	/**
	 * 라이선스 생성 시점 millis (Not Null)
	 */
	Instant createdAt;
	/**
	 * 라이선스 용량(단위:  hour)
	 * 기본 365*24 시간으로 제공
	 * 
	 * 라이선스 구매 후 활성화했을때 활성 시간과 만료 시간을 아래와 같이 계산함
	 * 
	 * activatedAt = current_time();
	 * expiredAt   = activatedAt + durationInHours * 60m * 60s * 1000ms
	 */
	Integer durationInHours;
	/**
	 * 라이선스 최초 사용 시점 millis
	 */
	Instant activatedAt;
	/**
	 * 라이선스 만료 시점 millis
	 */
	Instant expiredAt;
	/**
	 * 라이선스 사용자(선생님, 학부모 등)
	 */
	Long receiverRef;
	/**
	 * 라이선스 발급자
	 * 라이선스를 직접 구매한 경우 receiverRef와 동일, 무료 사용권의 경우 시스템 관리자의 PK로 설정됨
	 */
	Long issuerRef;
	/**
	 * 사용 가능한 라이선스인지 확인
	 * @param now
	 * @return
	 */
	public Boolean isAlive(Instant now) {
		if (activatedAt == null && expiredAt == null) {
			// 아직 사용 안함
			return Boolean.TRUE;
		}
		if (expiredAt.isAfter(now)) {
			// 사용됨. 만료 시간 확인
			return Boolean.TRUE;			
		}
		return Boolean.FALSE;
	}
	public boolean isAlreadyActivated() {
		return activatedAt != null;
	}
}
