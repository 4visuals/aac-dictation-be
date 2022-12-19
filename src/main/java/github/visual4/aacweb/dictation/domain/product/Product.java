package github.visual4.aacweb.dictation.domain.product;

import java.time.Instant;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Product {

	public final static Integer DUR_YEARL       = 24 * 30 * 12 + 5;
	public final static Integer DUR_HALF_YEARLY = 24 * 30 * 6;
	public final static Integer DUR_MONTHY      = 24 * 30;
	
	public enum Column {
		prod_seq,
		prod_code
	}
	
	Integer seq;
	String name;
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
	
	public boolean isBeta() {
		return "B".equals(this.type); 
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
	
}
