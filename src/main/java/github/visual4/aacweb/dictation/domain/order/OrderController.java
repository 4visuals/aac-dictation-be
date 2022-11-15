package github.visual4.aacweb.dictation.domain.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
		System.out.println(form);
		String code = form.getStr("productCode");
		Integer qtt = form.getInt("quantity");
		Order order = orderSerivce.createBetaOrder(teacherSeq.longValue(), code, qtt);
		
		orderSerivce.activateOrder(order.getOrderUuid(), 1L);
		return Res.success("order", order);
		
	}
	
}