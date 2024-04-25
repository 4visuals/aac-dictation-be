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
import github.visual4.aacweb.dictation.domain.order.delivery.DeliveryInfo;
import github.visual4.aacweb.dictation.domain.order.dto.NewOrderDto;
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
			@RequestBody NewOrderDto form) {
		String code = form.getProductCode();
		DeliveryInfo delivery = form.getDelivery();
		final Integer numOfProduct = form.getQtt();
		Order order = orderSerivce.createOrder(teacherSeq.longValue(),
				code,
				numOfProduct,
				odr -> odr.setDeliveryInfo(delivery));
		return Res.success("order", order);
	}
	/**
	 * 사용자가 주문을 취소함 - 주문 진행중에 취소됨(결제 시도 중 결제팝업을 닫음)
	 * @return
	 */
	@PutMapping("/order")
	public Object cancelOrder(
			@JwtProp("useq") Integer teacherSeq,
			@RequestBody TypeMap form) {
		String orderUuid = form.getStr("orderUuid");
		Order order = orderSerivce.cancelPendingOrder(orderUuid);
		return Res.success("order", order);
	}
	/**
	 * 주문 상세 정보
	 * @return
	 */
	@GetMapping("/order/{orderUuid}")
	public Object orderDetail(
			@JwtProp("useq") Integer teacherSeq,
			@PathVariable String orderUuid) {
		Order order = orderSerivce.findOrderDetail(orderUuid,
				TypeMap.with("product", true, "license", true));
		return Res.success("order", order);
	}
	/**
	 * 클라이언트 화면에서 결제를 시작하려고 함.
	 * 포트원에 결제 검증 코드를 전송함
	 * @return
	 */
	@PostMapping("/order/{orderUuid}/prepare")
	public Object sendPaymentVerification(
			@JwtProp("useq") Integer teacherSeq,
			@PathVariable String orderUuid) {
		orderSerivce.sendPaymentVerification(teacherSeq.longValue(), orderUuid);
		return Res.success(true);
	}
	/**
	 * 사용자 화면에 노출되는 주문 내역(active, ready)인 주문만 조회함
	 * @param teacherSeq
	 * @return
	 */
	@GetMapping("/orders")
	public Object listOfOrders(@JwtProp("useq") Integer teacherSeq) {
		List<Order> orders = orderSerivce.findPurchasedOrders(teacherSeq.longValue());
		return Res.success("orders", orders);
	}
}
