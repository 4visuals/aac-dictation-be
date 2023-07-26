package github.visual4.aacweb.dictation.domain.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.Res;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.web.aop.JwtProp;

@RestController
@RequestMapping("/api")
public class OrderController {
	
	@Autowired
	OrderService orderSerivce;

	@PostMapping("/order/beta")
	public Object createBetaOrder(@JwtProp("useq") Integer teacherSeq, @RequestBody TypeMap form) {
//		System.out.println(form);
//		String code = form.getStr("productCode");
//		Integer qtt = form.getInt("quantity");
//		Order order = orderSerivce.createBetaOrder(teacherSeq.longValue(), code, qtt);
//		
//		orderSerivce.activateOrder(order.getOrderUuid(), 1L, 1, null);
//		return Res.success("order", order);
		throw new AppException(ErrorCode.ORDER_BETA_CLOSED, 400);
		
	}
	/**
	 * 새로운 주문 준비 - 결제 진행을 위해 주문 인스턴스를 생성함
	 * @param teacherSeq
	 * @param form
	 * @return
	 */
	@PostMapping("/order")
	public Object prepareOrder(
			@JwtProp("useq") Integer teacherSeq,
			@RequestBody TypeMap form) {
		String code = form.getStr("productCode");
		final Integer numOfProduct = 1;
//		final Integer numOfLicenses = 1;
		Order order = orderSerivce.createOrder(teacherSeq.longValue(),
				code,
				numOfProduct,
				null);
		return Res.success("order", order);
	}
	/**
	 * 주문 취소 - 주문 진행중에 취소됨
	 * @return
	 */
	@PutMapping("/order")
	public Object cancelOrder(
			@JwtProp("useq") Integer teacherSeq,
			@RequestBody TypeMap form) {
		String orderUuid = form.getStr("orderUuid");
		Order order = orderSerivce.cancelOrder(orderUuid);
		return Res.success("order", order);
	}
	/**
	 * 주문 상세 정보
	 * @return
	 */
	@GetMapping("/order/{orderUuid}")
	public Object orderDetail(@JwtProp("useq") Integer teacherSeq,
			@PathVariable String orderUuid) {
		Order order = orderSerivce.findOrderDetail(orderUuid,
				TypeMap.with("product", true, "license", true));
		return Res.success("order", order);
	}
	
	@GetMapping("/orders")
	public Object listOfOrders(@JwtProp("useq") Integer teacherSeq) {
		List<Order> orders = orderSerivce.findPurchasedOrders(teacherSeq.longValue());
		return Res.success("orders", orders);
	}
}
