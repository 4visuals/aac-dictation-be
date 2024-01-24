package github.visual4.aacweb.dictation.domain.order;

import lombok.Getter;
import lombok.Setter;
/**
 * 주문 처리
 * @author chminseo
 *
 */
@Getter
@Setter
public class OrderCommitDto {
	/**
	 * 단체구매 문의(GroupOrderForm) 참조
	 */
	Integer groupOrderSeq;
	/**
	 * 제품UUID(Product.uuid)
	 */
	String productCode;
	/**
	 * 수량
	 */
	Integer qtt;
	/**
	 * 약정 금액.
	 * 정상가격(제품가격x수량)에 대해서 단체 구매시 고객과 합의한 납부 금액
	 */
	Integer contractPrice;
	public boolean isInvalidPrice() {
		return this.contractPrice == null || this.contractPrice == 0;
	}
}
