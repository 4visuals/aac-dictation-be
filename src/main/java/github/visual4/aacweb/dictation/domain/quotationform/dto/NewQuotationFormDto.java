package github.visual4.aacweb.dictation.domain.quotationform.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewQuotationFormDto {

	String origin;
	LocalDate issueDate;
	String orgName;
	String adminName;
	String adminEmail;
	String adminPhone;
	String greamEmail;
	String requesterName;
	String requesterEmail;
	String requesterPhone;
	Integer totalAmount;
	List<ItemDto> items;

	@Getter
	@Setter
	public static class ItemDto {
		Integer seq;
		String productName;
		String productCode;
		Integer qty;
		Integer unitPrice;
		Integer amount;
	}
}
