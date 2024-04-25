package github.visual4.aacweb.dictation.domain.order.delivery;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryInfo {
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
}
