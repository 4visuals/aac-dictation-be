package github.visual4.aacweb.dictation.domain.license;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import github.visual4.aacweb.dictation.BaseDao;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.product.Product;

@Import({LicenseDao.class, LicenseService.class})
public class LicenseServiceTest extends BaseDao {

	@Import({LicenseDao.class, LicenseService.class})
	public static class Imports {}
	@Autowired
	LicenseService lcsService;
	
	@Test
	void test_발급() {
		Product prod = new Product();
		prod.setSeq(1);
		List<License> licenses = lcsService.createLicenses(2, null, (lcs) -> {
			lcs.setIssuerRef(1L);
			lcs.setReceiverRef(2L);
		});
		assertEquals(2, licenses.size());
		licenses.forEach(lcs -> assertNotNull(lcs.getSeq()));
	}
	
	@DisplayName("이용권과 주문 함께 조회")
	@Test
	void test_licenses_with_order() {
		TypeMap map = lcsService.findLicensesWithOrder(46L);
		System.out.println(map);
	}
	

}
