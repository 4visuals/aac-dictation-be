package github.visual4.aacweb.dictation.domain.product;

import java.time.Instant;
import java.util.function.Consumer;

import org.springframework.context.annotation.Import;

import github.visual4.aacweb.dictation.ProductSalesType;
import github.visual4.aacweb.dictation.domain.appconfig.ImportAppConfig;

@Import({ProductService.class, ProductDao.class, ImportAppConfig.class})
public class ImportProduct {

	public static Product retailProduct(Consumer<Product> hook) {
		Product p = new Product();
		p.setName("Dummy Product");
		p.setDescription("For test");
		p.setDiscountKrWon(0);
		p.setDurationInHours(100);
		p.setPriceKrWon(1000);
		p.setSalesType(ProductSalesType.RT);
		p.setType("S");
		if (hook != null) {
			hook.accept(p);
		}
		return p;
	}

	public static Product groupBuyingProduct() {
		return retailProduct((prod) -> prod.setSalesType(ProductSalesType.GB));
	}
}
