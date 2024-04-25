package github.visual4.aacweb.dictation.domain.order.aim_port;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AimPortServiceTest {
	
	@Test
	void test_confirmPayments() {
		AimportHook hook = new AimportHook();
		hook.aimportUuid = "imp_780163825697";
		hook.orderUuid = "odr-8ed5ef92-4571-4761-8fed-66a7e10c4360";
		hook.status = "paid";
		
		AimPortService service = new AimPortService(null,null,  null, null, null);
		
		service.confirmPayment(hook);
	}
	
	@Test
	public void test_msg () {
		String s = "\uc874\uc7ac\ud558\uc9c0 \uc54a\ub294 \uacb0\uc81c\uc815\ubcf4\uc785\ub2c8\ub2e4";
		System.out.println(s);
	}

}
