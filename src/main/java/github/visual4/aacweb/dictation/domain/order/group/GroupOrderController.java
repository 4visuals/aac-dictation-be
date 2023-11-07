package github.visual4.aacweb.dictation.domain.order.group;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.visual4.aacweb.dictation.Res;
import github.visual4.aacweb.dictation.web.aop.JwtProp;
/**
 * 단체 주문 관련 uri
 * 관리자가 단체 주문을 처리하는 코드는 AdminController에 있음.
 * @author chminseo
 *
 */
@RestController
@RequestMapping("/api/group-order")
public class GroupOrderController {

	final GroupOrderService groupOrderService;
	
	public GroupOrderController(GroupOrderService groupOrderService) {
		this.groupOrderService = groupOrderService;
	}
	/**
	 * 고객이 단체 주문 양식을 제출함
	 * @return
	 */
	@PostMapping("/contact")
	public Object newGroupOrderForm(
			@JwtProp("useq") Integer teacherSeq,
			@RequestBody GroupOrderForm orderForm) {
		
		orderForm.setSenderRef(teacherSeq.longValue());
		groupOrderService.createGroupOrderForm(teacherSeq.longValue(), orderForm, true);
		
		return Res.success("orderForm", orderForm);
		
	}
}
