package github.visual4.aacweb.dictation.domain.order.group;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import github.visual4.aacweb.dictation.BaseDao;
import github.visual4.aacweb.dictation.domain.license.LicenseServiceTest;
import github.visual4.aacweb.dictation.domain.order.OrderServiceTest;
import github.visual4.aacweb.dictation.domain.product.ProductServiceTest;
import github.visual4.aacweb.dictation.domain.user.UserServiceTest;
import github.visual4.aacweb.dictation.service.UuidService;
import github.visual4.aacweb.dictation.service.UuidServiceTest;
import github.visual4.aacweb.dictation.service.codec.Sha256Codec;
import github.visual4.aacweb.dictation.service.mailing.MailingService;
import github.visual4.aacweb.dictation.service.mailing.MailingServiceTest;

@Import(GroupOrderServiceTest.Imports.class)
public class GroupOrderServiceTest extends BaseDao {

	@Import({
		GroupOrderService.class,
		GroupOrderDao.class,
		GroupPaperDao.class,
		UuidServiceTest.Imports.class,
		ProductServiceTest.Imports.class,
		OrderServiceTest.Imports.class,
		UserServiceTest.Imports.class,
		LicenseServiceTest.Imports.class,
		MailingServiceTest.Imports.class
		
	})
	public static class Imports {}
	
	@Autowired
	GroupOrderService gOrderService;
	
	@Test
	void test() {
		assertNotNull(gOrderService);
	}

}
