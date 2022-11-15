package github.visual4.aacweb.dictation.domain.product;

import java.time.Instant;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;

public class Products {

	public static void assertProduct(Product product) {
		Integer price = product.getPriceKrWon();
		if (price == null || price < 0) {
			throw new AppException(ErrorCode.VALUE_MISMATCH, 400, "invalid price");
		}
		Integer discount = product.getDiscountKrWon();
		if (discount == null || discount < 0) {
			throw new AppException(ErrorCode.VALUE_MISMATCH, 400, "invalid discount");
		}
		if (price - discount <= 0) {
			throw new AppException(ErrorCode.VALUE_MISMATCH, 400, "not allowed, discount >= price");
		}
		if (product.isBeta()) {
			throw new AppException(ErrorCode.VALUE_MISMATCH, 400, "beta product not allowed");
		}
		Instant activationTime = product.getActivatedAt();
		if (activationTime == null) {
			throw new AppException(ErrorCode.VALUE_MISMATCH, 400, "set activation date");
		}
		if (product.getCode() == null) {
			throw new AppException(ErrorCode.VALUE_MISMATCH, 400, "product code required");
		}
		if (product.getCreatedBy() == null) {
			throw new AppException(ErrorCode.VALUE_MISMATCH, 400, "creator email required");
		}
		Integer duration = product.getDurationInHours() ;
		if (duration== null || duration <= 0) {
			throw new AppException(ErrorCode.VALUE_MISMATCH, 400, "duration is " + duration);
		}
		String name = product.getName();
		if (product.getName() == null || name.trim().length() == 0) {
			throw new AppException(ErrorCode.VALUE_MISMATCH, 400, "null or empty product name");
		}
		product.setName(name.trim());
		String type = product.getType();
		if (type == null || !"S".equals(type)) {
			throw new AppException(ErrorCode.VALUE_MISMATCH, 400, "type should be \"S\"");
		}
		
	}

}
