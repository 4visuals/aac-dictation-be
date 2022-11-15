package github.visual4.aacweb.dictation.domain.order;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.domain.appconfig.AppConfigService;
import github.visual4.aacweb.dictation.domain.appconfig.AppConfiguration;
import github.visual4.aacweb.dictation.domain.license.License;
import github.visual4.aacweb.dictation.domain.license.LicenseService;
import github.visual4.aacweb.dictation.domain.product.Product;
import github.visual4.aacweb.dictation.domain.product.ProductService;
import github.visual4.aacweb.dictation.domain.user.User;
import github.visual4.aacweb.dictation.domain.user.UserService;

@Service
@Transactional
public class OrderService {
	final private static Integer TRIAL_PRODUCT_SEQ = 2;
	final AppConfigService configService;
	final ProductService productService;
	final UserService userService;
	final LicenseService licenseService;
	final OrderDao orderDao;
	
	public OrderService(
			ProductService productService,
			AppConfigService configService,
			UserService userService, 
			LicenseService licenseService,
			OrderDao orderDao) {
		this.productService = productService;
		this.configService = configService;
		this.userService = userService;
		this.licenseService = licenseService;
		this.orderDao = orderDao;
	}
	public Order createBetaOrder(Long teacherSeq, String productCode, Integer qtt) {
		User teacher = userService.findTeacher(teacherSeq);
		
		Product product = productService.findBy(Product.Column.prod_code, productCode);
		if (product == null) {
			throw new AppException(ErrorCode.ORDER_INVALID_PROD, 422, productCode);
		}
		if (!product.isBeta()) {
			throw new AppException(ErrorCode.ORDER_INVALID_PROD, 422, productCode);
		}
		
		AppConfiguration config = configService.getConfiguration();
		
		Order order = new Order();
		/* 상품 관련*/
		order.setProductRef(product.getSeq());
		order.setTotalAmount(product.getPriceKrWon() * qtt);
		
		/* times */
		Instant cur = Instant.now();
		Instant paymentDueTime = config.getPaymentDueTime(cur);
		order.setOrderTime(cur);
		order.setPaymentDueTime(paymentDueTime);
		order.setPaidTime(null); // 관리자가 결제 승인할때 설정됨
		
		order.setCustomerRef(teacher.getSeq());
		order.setConfirmerRef(null); // 관리자가 결제 승인할때 설정됨
		order.setOrderUuid("odr-" + UUID.randomUUID().toString());
		
		System.out.println(order.getTotalAmount());
		orderDao.insertOrder(order);
		
		/* licenses */
		List<License> items = licenseService.createLicenses(product, qtt, order, (lcs) -> {
			lcs.setIssuerRef(config.getAdminAccountSeq());
			lcs.setReceiverRef(teacher.getSeq());
			lcs.setCreatedAt(cur);
			lcs.setDurationInHours(License.BETA_DURATION);
		});
		order.setItems(items);
		
		return order;
	}
	/**
	 * 주문 등록
	 * @param teacherSeq
	 * @param productCode
	 * @param qtt - 갯수
	 * @return
	 */
	public Order createOrder(Long teacherSeq, String productCode, Integer qtt) {
		User teacher = userService.findTeacher(teacherSeq);
		
		Product product = productService.findBy(Product.Column.prod_code, productCode);
		if (product == null) {
			throw new AppException(ErrorCode.ORDER_INVALID_PROD, 422, productCode);
		}
		
		AppConfiguration config = configService.getConfiguration();
		
		Order order = new Order();
		/* 상품 관련*/
		order.setProductRef(product.getSeq());
		order.setTotalAmount(product.calculateUnitPrice() * qtt);
		
		/* times */
		Instant cur = Instant.now();
		Instant paymentDueTime = config.getPaymentDueTime(cur);
		order.setOrderTime(cur);
		order.setPaymentDueTime(paymentDueTime);
		order.setPaidTime(null); // 관리자가 결제 승인할때 설정됨
		
		order.setCustomerRef(teacher.getSeq());
		order.setConfirmerRef(null); // 관리자가 결제 승인할때 설정됨
		order.setOrderUuid("odr-" + UUID.randomUUID().toString());
		
		System.out.println(order.getTotalAmount());
		orderDao.insertOrder(order);
		
		/* licenses */
		List<License> items = licenseService.createLicenses(product, qtt, order, (lcs) -> {
			lcs.setIssuerRef(config.getAdminAccountSeq());
			lcs.setReceiverRef(teacher.getSeq());
			lcs.setCreatedAt(cur);
			lcs.setDurationInHours(product.getDurationInHours());
		});
		order.setItems(items);
		
		return order;
	}
	/**
	 * 평가판 주문 생성(라이선스 발급도 함께)
	 * @param user
	 * @return
	 */
	public Order createTrialOrder(User user) {
		Product trialProduct = productService.findBy(
				Product.Column.prod_seq,
				TRIAL_PRODUCT_SEQ);
		Order order = createOrder(user.getSeq(), trialProduct.getCode(), 1);
		activateOrder(order.getOrderUuid(), 1L);
		return order;
	}
	/**
	 * 주문을 승인함
	 * @param order
	 * @param adminSeq
	 */
	public void activateOrder(String orderCode, Long adminSeq) {
		Order order = orderDao.findBy(Order.Column.order_uuid, orderCode);
		order.markAsActivated(Instant.now(), adminSeq);
		orderDao.activateOrder(order);
	}
	public List<Order> findOrdersWithProduct() {
		List<Order> orders = orderDao.findOrders();
		return orders;
	}
}
