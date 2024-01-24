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
	
	public enum OrderState {
		/**
		 * READY - 주문 생성 후 결제 전 대기 상태
		 */
		RDY,
		/**
		 * ACTIVE - 생성된 주문에 대해 결제가 완료됨, mid_trx_uid, end_trx_uid, trx_detail, pg_vendor등에 유의미한 값이 존재함
		 */
		ATV,
		/**
		 * CANCEL BY USER - 사용자가 결제를 취소함(사용자가 결제 팝업을 띄운 후 결제를 끝마치지 않고 팝업을 닫음)
		 */
		CNU,
		/**
		 * CANCEL BY ERROR - 결제 중 오류 발생으로 취소됨
		 */
		CNE,
		/**
		 * CANCEL BY REMOTE - 포트원(아임포트) 관리자 화면에서 결제를 취소한 경우.(webhook을 통해서 취소 결과가 인입됨)
		 */
		CNR,
	}
	public enum Column {
		order_uuid, customer_ref
	}
	/**
	 * PK
	 */
	Integer seq;
	/**
	 * 상품 참조
	 */
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
	
	Boolean trialOrder;
	
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
	/**
	 * 주문 상태
	 */
	OrderState orderState;
	/**
	 * 결제 대행 업체 정보
	 */
	PG paygateVendor;
	/**
	 * 결제 후 생성된 상세 로그
	 */
	String transactionDetail;
	/**
	 * 거래 식별 코드 1치 (아임포트에서 발행한 거래 식별 코드) 
	 */
	String midTransactionUid;
	/**
	 * 거래 식별 코드 2차(실제로 거래를 승인한 pg사에서 발행한 식별 코드)
	 */
	String endTransactionUid;
	
	Integer licenseQtt;
	
	
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
	/**
	 * 대기중인 주문인지(결제 진행을 위해) 나타냄 
	 * @return
	 */
	public boolean isPending() {
		return this.orderState == OrderState.RDY;
	}
	public boolean isActive() {
		return this.orderState == OrderState.ATV;
	}
	public boolean isError() {
		return this.orderState == OrderState.CNE;
	}
	/**
	 * 사용자가 결제 도중에 취소함
	 */
//	public void cancelByUser() {
//		if (this.orderState == OrderState.RDY) {
//			this.orderState = OrderState.CNU;
//		}
//	}

	public void bindProduct(Product product) {
		this.product = product;
		this.productRef = null;
		
	}

	public void bindItems(List<License> licenses) {
		this.items = licenses;
		this.itemCount = null;
	}

	public boolean isGroupOrder() {
		return this.paygateVendor == PG.group_order || this.licenseQtt > 1;
//		return false;
	}
}
