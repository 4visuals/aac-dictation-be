package github.visual4.aacweb.dictation.domain.license;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import github.visual4.aacweb.dictation.BaseDao;
import github.visual4.aacweb.dictation.domain.product.Product;

@Import({LicenseDao.class, LicenseService.class})
class LicenseServiceTest extends BaseDao {

	@Autowired
	LicenseService lcsService;
	
	@Test
	void test_발급() {
		Product prod = new Product();
		prod.setSeq(1);
		List<License> licenses = lcsService.createLicenses(prod, 2, null, (lcs) -> {
			lcs.setIssuerRef(1L);
			lcs.setReceiverRef(2L);
		});
		assertEquals(2, licenses.size());
		licenses.forEach(lcs -> assertNotNull(lcs.getSeq()));
	}

}
