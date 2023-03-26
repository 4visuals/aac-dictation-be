package github.visual4.aacweb.dictation.domain.order.group;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * 단체 구매 주문서
 * @author chminseo
 *
 */
@Getter
@Setter
@ToString
public class GroupOrderForm {
	public enum Column {
		seq, group_order_state
	}
	public enum OrderFormState {
		/**
		 * 대기 - 사용자가 단체 구매 주문을 등록함. 아직 처리되지 않음.
		 */
		PND,
		/**
		 * 결제확인 - 사용자 입금 확인 후 관리자가 수강증 발급을 완료함.
		 */
		CMT,
		/**
		 * 사용자 취소 - 사용자가 구매 주문을 취소함
		 */
		CBU,
		/**
		 * 시스템 취소 - 입금이 확인되지 않음. 시스템 관리자가 취소시킴
		 */
		CBS
	}
	/**
	 * PK
	 */
	Integer seq;
	/**
	 * 기관명
	 */
	String orgName;
	/**
	 * 문의자이름
	 */
	String senderName;
	/**
	 * 문의자 연락처
	 */
	String senderContactInfo;
	/**
	 * 문의자 이메일
	 */
	String senderEmail;
	/**
	 * 기관 이메일(서류 보낼 곳)
	 */
	String orgEmail;
	/**
	 * 문의 내용
	 */
	String content;
	/**
	 * 삭별경로값
	 */
	String orderUuid;
	/**
	 * 주문 생성 시간
	 */
	Instant creationTime;
	
	String creationTimeKr;
	
	/**
	 * 주문의 현재 상태
	 */
	OrderFormState state;
	/**
	 * FK: dt_users참조
	 */
	Long senderRef;
	/**
	 * M:1 dt_users 인스턴스
	 */
	User sender;
	/**
	 * 증빙 서류 목록
	 */
	List<OrderPaper> papers;
	/**
	 * 주문 조회 url
	 */
	String orderFullUrl;
	
	public void setCreationTime(Instant time) {
		this.creationTime = time;
		/*
		 * 템플릿 출력용
		 */
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
		this.creationTimeKr = formatter.format(time);
	}

	public void cancel(OrderFormState formState) {
		if (this.state != OrderFormState.PND) {
			throw new AppException(ErrorCode.GROUP_ORDER_ERROR, 422, "not pending state");
		}
		this.state = formState;
	}
	/**
	 * 결제 완료 상태로 변경
	 */
	public void commit() {
		if (this.state != OrderFormState.PND) {
			throw new AppException(ErrorCode.GROUP_ORDER_ERROR, 422, "not pending state");
		}
		this.state = OrderFormState.CMT;
	}
}
