package github.visual4.aacweb.dictation.domain.order.delivery;

import java.util.regex.Pattern;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryInfo {
	
	private static final String [] PHONE = {"^\\d{2,3}$", "^\\d{3,4}$", "^\\d{4}$"};
	
	/**
	 * PK
	 */
	Integer seq;
	/**
	 * 도로명 주소로 통일함
	 * ex) xx시 xx구 xx로 2길
	 */
	String baseAddress;
	/**
	 * ex) 2층 201호
	 */
	String detailAddress;
	/**
	 * FK to users.seq(기록용)
	 */
	Long userRef;
	/**
	 * 택배수령인 이름
	 */
	String receiverName;
	/**
	 * 5자리 우편번호
	 */
	String zipCode;
	/**
	 * 연관 주문
	 * FK to orders.seq
	 */
	Integer orderRef;
	
	String phoneNumber;

	public void checkPhoneNumber() {
		String  pn = this.phoneNumber;
		if(pn == null ) {
			throw new AppException(ErrorCode.ORDER_DELIVERY_ERROR, 400, "null phone number");
		}
		pn = pn.trim();
		if(pn.length() == 0 ) {
			throw new AppException(ErrorCode.ORDER_DELIVERY_ERROR, 400, "empty phone number");
		}
		String [] parts = pn.split("-");
		if(parts.length != 3) {
			throw new AppException(ErrorCode.ORDER_DELIVERY_ERROR, 400, "check phone number");
		}
		boolean valid = Pattern.matches(PHONE[0], parts[0].trim()) && Pattern.matches(PHONE[1], parts[1].trim()) && Pattern.matches(PHONE[2], parts[2].trim()); 
		if (!valid) {
			throw new AppException(ErrorCode.ORDER_DELIVERY_ERROR, 400, "check phone number");
		}
		this.phoneNumber = parts[0].trim()  + "-" + parts[1].trim() + "-" + parts[2].trim();
		
	}
}
