package github.visual4.aacweb.dictation.domain.quotationform;

import java.time.Instant;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class QuotationFormItem {

	public enum Column {
		qi_seq,
		qf_seq,
		prod_seq,
		prod_name,
		prod_code,
		qty,
		unit_price,
		amount,
		created_at,
		updated_at
	}

	@EqualsAndHashCode.Include
	Long seq;
	Long formRef;
	Integer productRef;
	String productName;
	String productCode;
	Integer qty;
	Integer unitPrice;
	Integer amount;
	Instant createdAt;
	Instant updatedAt;
}
