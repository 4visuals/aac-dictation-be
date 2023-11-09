package github.visual4.aacweb.dictation.domain.order;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import github.visual4.aacweb.dictation.domain.order.aim_port.AimportDriver;
import github.visual4.aacweb.dictation.domain.product.ProductServiceTest;
import github.visual4.aacweb.dictation.domain.user.UserServiceTest;

public class OrderServiceTest {

	@Import({
		OrderService.class,
		OrderDao.class,
		AimportDriver.class,
		ProductServiceTest.Imports.class,
		UserServiceTest.Imports.class,
	})
	public final static class Imports {}
	
	@Test
	void test() {
		fail("Not yet implemented");
	}

}
