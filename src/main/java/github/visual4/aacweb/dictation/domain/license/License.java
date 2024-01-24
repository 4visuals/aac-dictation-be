package github.visual4.aacweb.dictation.domain.license;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.domain.order.Order.OrderState;
import github.visual4.aacweb.dictation.domain.user.User;
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
		lcs_uuid,
		student_ref,
		order_ref
	}
	
	final public static int UNLIMITED = 24*365*10;

	public static final Integer BETA_DURATION = 24 * 3 * 30; // 90일

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
	 * 평가판 라이선스인지 나타냄
	 */
	Boolean trialVersion;
	/**
	 * 라이선스 결제 상태
	 * @derived - Order.orderState
	 */
	OrderState orderState;
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
	/**
	 * 주어진 선생님의 수강증인지 확인
	 * @param teacher
	 * @return
	 */
	public boolean isValidReceiver(User teacher) {
		return teacher.getSeq().equals(this.receiverRef);
	}
	
	public void markAsActive(Instant activeTime, Integer durationInHours) {
		/*
		 * 수강증 활성화 조건이 변경됨
		 * (활성화 = activeAt과 expiredAt 값이 존재함)
		 * 이전에는 학생을 연결할때 활성화되었으나, 구매 후 바로 활성화되므로 등록된 학생이 없을 수 있다.
		 * 
		 */
		if (this.activatedAt != null || this.expiredAt != null) {
			throw new AppException(ErrorCode.LICENSE_ALREADY_ACTIVATED, 400, "no student");
		}
		this.activatedAt = activeTime;
		this.durationInHours = durationInHours;
		this.expiredAt = activeTime.plus(durationInHours, ChronoUnit.HOURS);
	}
}
