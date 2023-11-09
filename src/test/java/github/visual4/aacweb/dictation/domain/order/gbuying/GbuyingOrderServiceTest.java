package github.visual4.aacweb.dictation.domain.order.gbuying;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.databind.ObjectMapper;

import github.visual4.aacweb.dictation.Asserts;
import github.visual4.aacweb.dictation.BaseDao;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.Util;
import github.visual4.aacweb.dictation.domain.order.Order;
import github.visual4.aacweb.dictation.domain.order.group.GroupOrderForm;
import github.visual4.aacweb.dictation.domain.order.group.GroupOrderServiceTest;
import github.visual4.aacweb.dictation.domain.product.Product;
import github.visual4.aacweb.dictation.domain.product.ProductService;
import github.visual4.aacweb.dictation.domain.product.ProductServiceTest;
import github.visual4.aacweb.dictation.domain.user.User;
import github.visual4.aacweb.dictation.domain.user.UserServiceTest;


@Import(GbuyingOrderServiceTest.Imports.class)
class GbuyingOrderServiceTest extends BaseDao {
	
	@Import({
		GbuyingOrderService.class,
		
		ProductServiceTest.Imports.class,
		GroupOrderServiceTest.Imports.class,
		UserServiceTest.Imports.class
	})
	public static class Imports {}

	public static class Sample {
		public static GroupOrderForm sample(User user, String price, String qtt, ObjectMapper om) {
			GroupOrderForm form = new GroupOrderForm();
			TypeMap content = TypeMap.with("price", price, "qtt", qtt, "userRef", user.getSeq());
			form.setContent(Util.stringify(om, content));
			form.setSenderName(user.getName());
			form.setSenderEmail(user.getEmail());
			return form;
		}
	}
	@Autowired
	GbuyingOrderService orderService;
	
	@Autowired
	ProductService productService;
	
	@Autowired
	ObjectMapper om;

	@Test
	void test_notnull() {
		Asserts.notNulls(orderService, om);
	}
	/**
	 * 등록된 공구 문의마다 주문을 생성함
	 */
	@Test
	void test_create_orders() {
		User admin = UserServiceTest.Sample.admin();
		/* 1. 상품 생성 */
		Product product = ProductServiceTest.Sample.groupBuyingProduct();
		productService.createProduct(admin, product);
		
		/* 2. 단체 구매 문의 접수 */
		User teacher = UserServiceTest.Sample.teacher1();
		GroupOrderForm form = Sample.sample(teacher, "95000", "8", om);
		form.setProductRef(product.getSeq());
		
		form = orderService.insertOrderForm(form);
		assertNotNull(form.getSeq());
		
		/* 3. 주문 생성*/
		List<Order> orders = orderService.createOrders(admin, product.getCode());
		assertEquals(1, orders.size());
//		assertEquals(teacher.getSeq(), orders.get(0).getCustomerRef());
	}
}
