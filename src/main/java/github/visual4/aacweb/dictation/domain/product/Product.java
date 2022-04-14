package github.visual4.aacweb.dictation.domain.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Product {

	public enum Column {
		prod_seq,
		prod_code
	}
	
	Integer seq;
	String name;
	String code;
	Integer priceKrWon;
	String description;
	
}
