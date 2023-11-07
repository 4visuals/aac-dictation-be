package github.visual4.aacweb.dictation.domain.admin;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.visual4.aacweb.dictation.Res;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.order.gbuying.GroupBuyingOrderService;
import github.visual4.aacweb.dictation.domain.order.group.GroupOrderForm;
import github.visual4.aacweb.dictation.domain.product.Product;
import github.visual4.aacweb.dictation.domain.product.ProductService;
import github.visual4.aacweb.dictation.domain.user.User;
import github.visual4.aacweb.dictation.domain.user.UserService;
import github.visual4.aacweb.dictation.web.aop.JwtProp;

@RestController
@RequestMapping("/api/admin/gbuying")
public class AdminGroupBuyingController {

	final GroupBuyingOrderService gBuyingService;
	final ProductService productSerivce;
	final UserService userService;
	public AdminGroupBuyingController(
			GroupBuyingOrderService gBuyingService,
			ProductService productSerivce,
			UserService userService) {
		this.gBuyingService = gBuyingService;
		this.productSerivce = productSerivce;
		this.userService = userService;
	}
	/**
	 * 공동구매 상품 생성(단체구매 양식을 사용함)
	 * @return
	 */
	@PostMapping("/product")
	public Object createGroupBuyingProduct(
			@JwtProp("useq") Integer adminSeq,
			@RequestBody Product product) {
		User admin = userService.loadAdmin(adminSeq.longValue());
		productSerivce.createProduct(admin, product);
		return Res.success("product", product);
	}
	/**
	 * 공구 상품에 접수한 구매 양식(실제 주문이 아님)
	 * @return
	 */
	@GetMapping("/product/{productUuid}/forms")
	public Object orderFormsOfProduct(
			@JwtProp("useq") Integer adminSeq,
			@PathVariable String productUuid) {
		userService.loadAdmin(adminSeq.longValue());
		List<GroupOrderForm> forms = gBuyingService.findOderForms(productUuid);
		return Res.success("forms", forms);
	}
	/**
	 * 공구 상품에 주문 양식 등록(실제 주문이 아님)
	 * @param adminSeq
	 * @param form
	 * @return
	 */
	@PostMapping("/form")
	public Object registerOrderForm(
			@JwtProp("useq") Integer adminSeq,
			@RequestBody GroupOrderForm form) {
		userService.loadAdmin(adminSeq.longValue());
		gBuyingService.insertOrderForm(form);
		return Res.success("form", form);
	}
	@PutMapping("/form/{formSeq}")
	public Object updateOrderForm(
			@JwtProp("useq") Integer adminSeq,
			@PathVariable Integer formSeq,
			@RequestBody TypeMap body) {
		userService.loadAdmin(adminSeq.longValue());
		GroupOrderForm form = gBuyingService.updateOrderForm(formSeq, body);
		return Res.success("form", form);
	}
	@DeleteMapping("/form/{formSeq}")
	public Object deleteOrderForm(
			@JwtProp("useq") Integer adminSeq,
			@PathVariable Integer formSeq) {
		userService.loadAdmin(adminSeq.longValue());
		gBuyingService.deleteOrderForm(formSeq);
		return Res.success(true);
	}
}
