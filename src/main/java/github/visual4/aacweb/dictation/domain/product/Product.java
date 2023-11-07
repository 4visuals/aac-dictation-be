package github.visual4.aacweb.dictation.domain.product;

import java.time.Instant;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.ProductSalesType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {

	public final static Integer DUR_YEARL       = 24 * 30 * 12 + 5;
	public final static Integer DUR_HALF_YEARLY = 24 * 30 * 6;
	public final static Integer DUR_MONTHY      = 24 * 30;
	
	public enum Column {
		prod_seq,
		prod_code
	}
	
	@EqualsAndHashCode.Include
	Integer seq;
	/**
	 * 상품명
	 */
	String name;
	/**
	 * product uuid
	 */
	String code;
	Integer priceKrWon;
	Integer discountKrWon;
	String description;
	String type;
	/**
	 * 상품 사용 시간(단위: hours)
	 * 8760 = 24 * 365
	 * 
	 */
	Integer durationInHours;
	
	Instant activatedAt;
	Instant expiredAt;
	String createdBy;
	/**
	 * 상품 디자인용 theme value("green", "picton-blue", "yellow", "violet", ...)
	 * 
	 */
	String theme;
	/**
	 * 소매용인지 공동구매용인지 나타냄 (See {@link https://github.com/4visuals/aac-writing/issues/151})
	 */
	ProductSalesType salesType;
	
	public boolean isBeta() {
		return "B".equals(this.type); 
	}
	public boolean isTrialProduct() {
		return isBeta();
	}
	/**
	 * 상품 최종 단가 반환
	 * @return 원가에서 할인액을 뺀 최종 금액
	 */
	public Integer calculateUnitPrice() {
		Integer price = priceKrWon - discountKrWon;
		if (price < 0) {
			throw new AppException(
					ErrorCode.PRODUCT_ERROR, 500,
					String.format(
							"negative price %d(base: %d, discount: %d)", 
							price,
							priceKrWon,
							discountKrWon) );
		}
		return price;
	}
	/**
	 * 기간 확인. 만료된 상품이면 true 반환함
	 * @param now
	 * @return
	 */
	public boolean checkExpiration(Instant time) {
		if (expiredAt == null) {
			// 민료 기간이 없음.
			return false;
		}
		return this.expiredAt.isAfter(time);
	}
	public Boolean checkIfRetail() {
		return this.salesType == ProductSalesType.RT;
	}
	public Boolean checkIfGroupBuying() {
		return this.salesType == ProductSalesType.GB;
	}
	public boolean hasValidSalesType() {
		return this.salesType != null;
	}
	
	
}
