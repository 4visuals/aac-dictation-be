package github.visual4.aacweb.dictation.domain.order;

import java.time.Instant;
import java.util.List;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.domain.license.License;
import github.visual4.aacweb.dictation.domain.product.Product;
import github.visual4.aacweb.dictation.domain.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Order {
	
	public enum Column {
		order_uuid
	}
	/**
	 * PK
	 */
	Integer seq;
	
	Integer productRef;
	Product product;
	
	/**
	 * 주문 발생 시간
	 */
	Instant orderTime;
	/**
	 * 결제 시한 - 주문 발생 이후 주어진 시간 내에 결제가 완료되어야 함
	 */
	Instant paymentDueTime;
	/**
	 * 결제 확인 시간 - 결제된 시간
	 */
	Instant paidTime;
	
	Integer totalAmount;
	
	Long customerRef;
	User customer;
	/**
	 * 결제 확인 및 승인한 관리자 - 결제를 승인할때 사용한 계정
	 */
	Long confirmerRef;
	
	List<License> items;
	
	Integer itemCount;
	/**
	 * 주문 식별 코드
	 */
	String orderUuid;
	
	public void setTotalAmount(Integer amount) {
		if (amount == null || amount < 0) {
			throw new AppException(ErrorCode.ORDER_INVALID_PRICE, 422);
		}
		this.totalAmount = amount;
	}

	public void markAsActivated(Instant activationTime, Long adminSeq) {
		if (this.paidTime != null || this.confirmerRef != null) {
			throw new AppException(ErrorCode.ORDER_ALREADY_ACTIVATED, 409);
		}
		this.paidTime = activationTime;
		this.confirmerRef = adminSeq;
	}
}
