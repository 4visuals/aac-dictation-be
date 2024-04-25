package github.visual4.aacweb.dictation.domain.order.dto;

import github.visual4.aacweb.dictation.domain.order.delivery.DeliveryInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewOrderDto {

	String productCode;
	DeliveryInfo delivery;
	Integer qtt;
	
}
