package github.visual4.aacweb.dictation.domain.quotationform;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class QuotationForm {

	public enum QuotationState {
		/**
		 * 이용권 발급 전(진행 중)
		 */
		OPEN,
		/**
		 * 견적서 처리 완료(종료)
		 */
		CLOSED
	}
	public enum CloseReason {
		/**
		 * 이용권 발급 완료로 종료
		 */
		ISSUED,
		/**
		 * 결제 미진행 등으로 중단
		 */
		STOPPED
	}

	public enum Column {
		qf_seq,
		origin,
		issue_date,
		org_name,
		admin_name,
		admin_email,
		admin_phone,
		requester_name,
		requester_email,
		requester_phone,
		total_amount,
		qf_state,
		close_reason,
		closed_at,
		created_at,
		updated_at
	}

	@EqualsAndHashCode.Include
	Long seq;
	String origin;
	LocalDate issueDate;
	String orgName;

	String adminName;
	String adminEmail;
	String adminPhone;
	String greamEmail;

	String requesterName;
	String requesterEmail;
	String requesterPhone;

	Integer totalAmount;
	QuotationState state;
	CloseReason closeReason;
	Instant closedAt;
	Instant createdAt;
	Instant updatedAt;
	List<QuotationFormItem> items;
}
