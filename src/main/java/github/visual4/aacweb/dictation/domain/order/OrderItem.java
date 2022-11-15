package github.visual4.aacweb.dictation.domain.order;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.domain.product.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItem {

	Integer seq;
	
	Integer productRef;
	Product product;
	
	Integer quantity;
	
	Integer customerRef;
	
	public void setQuantity(Integer qtt) {
		if (qtt == null || qtt <= 0) {
			throw new AppException(ErrorCode.ORDER_INVALID_QTT, 422);
		}
	}
	
}
