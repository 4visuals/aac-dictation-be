package github.visual4.aacweb.dictation.domain.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import github.visual4.aacweb.dictation.ProductSalesType;
import github.visual4.aacweb.dictation.Res;
import github.visual4.aacweb.dictation.Util;

@RestController
@RequestMapping("/api")
public class ProductController {

	@Autowired
	private ProductService productService;
	/**
	 * 소매 상품 조회(개별 판매용)
	 * @return
	 */
	@GetMapping("/products/retail")
	public Object listRetailProducts() {
		List<Product> products = productService.filterProducts(product -> product.checkIfRetail());
		Util.<Product>forEach(products, prod -> {
			prod.setSeq(null);
		});
		return Res.success("products", products);
	}
	/**
	 * 모든 상품 전부 조회
	 * @return
	 */
	@GetMapping("/products")
	public Object listProducts() {
		List<Product> products = productService.findProducts();
		Util.<Product>forEach(products, prod -> {
			prod.setSeq(null);
		});
		return Res.success("products", products);
	}
	/**
	 * 제품 상세 정보
	 * @param code
	 * @return
	 */
	@GetMapping("product")
	public Object findProduct(@RequestParam String code) {
		Product product = productService.findProductForPurchase(code);
		return Res.success("product", product);
	}
}
