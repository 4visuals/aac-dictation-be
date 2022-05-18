package github.visual4.aacweb.dictation.domain.admin;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.Util;
import github.visual4.aacweb.dictation.domain.license.License;
import github.visual4.aacweb.dictation.domain.license.LicenseService;
import github.visual4.aacweb.dictation.domain.product.Product;
import github.visual4.aacweb.dictation.domain.product.ProductService;
import github.visual4.aacweb.dictation.domain.user.User;
import github.visual4.aacweb.dictation.domain.user.UserService;

@Service
@Transactional
public class AdminService {
	final ProductService productService;
	final LicenseService licenseService;
	final UserService userService;
	final Long adminSeq;
	public AdminService(
			ProductService productService,
			LicenseService licenseService,
			UserService userService,
			@Value("${dictation.admin.seq}") Long adminSeq) {
		this.productService = productService;
		this.licenseService = licenseService;
		this.userService = userService;
		this.adminSeq = adminSeq;
	}
	/**
	 * 관리자가 직접 수강증 신규 발급
	 * @param userSeq
	 * @param qtt
	 * @return 
	 */
	public List<License> issueLicenses(Long userSeq, Integer qtt) {
		User adminAccount = userService.findUser(adminSeq);
		Product product = productService.findBy(Product.Column.prod_seq, 1);
		Util.notNull(product, ErrorCode.SERVER_ERROR, 500, "no such product seq(" + 1 + ")");
		
		// Instant expiredAt = Instant.now().plus(28, ChronoUnit.DAYS);
		List<License> licenses = licenseService.createLicenses(product, qtt, null, (lcs)-> {
			lcs.setIssuerRef(adminAccount.getSeq());
			lcs.setReceiverRef(userSeq);
			lcs.setDurationInHours(24*28); // 28일로 설정
		});
		return licenses;
	}

}
