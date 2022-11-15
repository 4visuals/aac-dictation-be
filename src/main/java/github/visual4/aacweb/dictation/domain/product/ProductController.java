package github.visual4.aacweb.dictation.domain.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.visual4.aacweb.dictation.Res;
import github.visual4.aacweb.dictation.Util;

@RestController
@RequestMapping("/api")
public class ProductController {

	@Autowired
	private ProductService productService;
	
	@GetMapping("/products")
	public Object listProducts() {
		List<Product> products = productService.findProducts();
		Util.<Product>forEach(products, prod -> {
			prod.setSeq(null);
		});
		return Res.success("products", products);
	}
}
