package github.visual4.aacweb.dictation.domain.appconfig;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppConfiguration {

	/**
	 * 계정당 무료 수강권 갯수
	 * 
	 * 계정 생성할때 주어진 갯수만큼의 수강권을 생성함
	 */
	Integer freeCertsPerUser;
	/**
	 * 결제 유예기간(시간단위)
	 * 
	 * 수강권 구매 후 결제하기까지 유예 시간.주어진 시간동안은 결제없이 기능을 사용할 수 있다. 
	 */
	long deferredPaymentDurationPerHour;
}
