package github.visual4.aacweb.dictation.domain.admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.visual4.aacweb.dictation.Res;
import github.visual4.aacweb.dictation.domain.product.Product;
import github.visual4.aacweb.dictation.domain.product.ProductService;
import github.visual4.aacweb.dictation.domain.user.User;
import github.visual4.aacweb.dictation.domain.user.UserService;
import github.visual4.aacweb.dictation.web.aop.JwtProp;

@RestController
@RequestMapping("/api/admin")
public class AdminProductController {

	@Autowired
	ProductService productService;
	@Autowired
	UserService userService;
	
	@GetMapping("/products")
	public Object listProducts(@JwtProp("useq") Integer userSeq) {
		User admin = userService.loadAdmin(userSeq.longValue());
		List<Product> products = productService.findProducts(admin);
		return Res.success("products", products);
	}
	/**
	 * 새로운 상품 등록
	 * @param userSeq
	 * @param product
	 * @return
	 */
	@PostMapping("/product")
	public Object createProduct(
			@JwtProp("useq") Integer userSeq, @RequestBody Product product) {
		User admin = userService.loadAdmin(userSeq.longValue());
		productService.createProduct(admin, product);
		return Res.success("product", product);
	}
	@PutMapping("/product")
	public Object updateProduct(
			@JwtProp("useq") Integer userSeq,
			@RequestBody Product product) {
		var prod = productService.updateBasicInfo(product);
		return Res.success("product", prod);
	}
}
