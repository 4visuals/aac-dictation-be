package github.visual4.aacweb.dictation.domain.admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

	final ProductService productService;
	final UserService userService;

    public AdminProductController(
    		ProductService productService,
    		UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }
	
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
	/**
	 * 등록된 상품 정보 수정
	 * @param userSeq
	 * @param product
	 * @return
	 */
	@PutMapping("/product")
	public Object updateProduct(
			@JwtProp("useq") Integer userSeq,
			@RequestBody Product product) {
		var prod = productService.updateBasicInfo(product);
		return Res.success("product", prod);
	}
	/**
	 * 등록된 상품을 만료 처리함(더이상 구매 불가)
	 * @param userSeq
	 * @param productSeq
	 * @return
	 */
	@DeleteMapping("/product/{productSeq}")
	public Object deleteProduct(
			@JwtProp("useq") Integer userSeq,
			@PathVariable Integer productSeq) {
		userService.loadAdmin(userSeq.longValue());
		productService.exipreProduct(productSeq);
		return Res.success(true);
	}
}
