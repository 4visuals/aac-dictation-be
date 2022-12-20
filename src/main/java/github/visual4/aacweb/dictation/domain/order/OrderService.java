package github.visual4.aacweb.dictation.domain.order;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.appconfig.AppConfigService;
import github.visual4.aacweb.dictation.domain.appconfig.AppConfiguration;
import github.visual4.aacweb.dictation.domain.license.License;
import github.visual4.aacweb.dictation.domain.license.LicenseService;
import github.visual4.aacweb.dictation.domain.order.Order.OrderState;
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
		List<License> items = licenseService.createLicenses( qtt, order, (lcs) -> {
			lcs.setIssuerRef(config.getAdminAccountSeq());
			lcs.setReceiverRef(teacher.getSeq());
			lcs.setCreatedAt(cur);
			lcs.setDurationInHours(License.BETA_DURATION);
		});
		order.setItems(items);
		
		return order;
	}
	
	/**
	 * 새로운 주문 생성. 생성한 주문은 결제가 이루어지기 전까지 READY상태로 남아있음.
	 * @param teacherSeq
	 * @param productCode
	 * @param qttProduct - 상품 갯수(보통 1개)
	 * @param qttLicenses - 수강증 갯수
	 * @return
	 */
	public Order createOrder(Long teacherSeq, String productCode, Integer qttProduct, Integer qttLicenses) {
		User teacher = userService.findTeacher(teacherSeq);
		
		Product product = productService.findBy(Product.Column.prod_code, productCode);
		if (product == null) {
			throw new AppException(ErrorCode.ORDER_INVALID_PROD, 422, productCode);
		}
		
		AppConfiguration config = configService.getConfiguration();
		
		Order order = new Order();
		/* 상품 관련*/
		order.setProductRef(product.getSeq());
		order.setTotalAmount(product.calculateUnitPrice() * qttProduct);
		
		/* times */
		Instant cur = Instant.now();
		Instant paymentDueTime = config.getPaymentDueTime(cur);
		order.setOrderTime(cur);
		order.setPaymentDueTime(paymentDueTime);
		order.setPaidTime(null); // 관리자가 결제 승인할때 설정됨
		
		order.setCustomerRef(teacher.getSeq());
		order.setConfirmerRef(null); // 관리자가 결제 승인할때 설정됨
		order.setOrderUuid("odr-" + UUID.randomUUID().toString());
		
		order.setOrderState(OrderState.RDY);
		order.setPaygateVendor("im_port");
//		System.out.println(order.getTotalAmount());
		orderDao.insertOrder(order);
		
		/* licenses
		 * 주문을 확인한 후에 수강증을 발급함  
		 */
		order.setItems(Collections.emptyList());
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
		Order order = createOrder(user.getSeq(), trialProduct.getCode(), 1, 1);
		return activateOrder(order.getOrderUuid(), 1L, 1, null);
	}
	/**
	 * 주문을 승인함. 수강증 생성
	 * @param orderCode
	 * @param confirmerSeq
	 * @param qttLicenses
	 * @param fn
	 * @return
	 */
	public Order activateOrder(
			String orderCode,
			Long confirmerSeq,
			final Integer qttLicenses,
			Consumer<Order> fn) {
		Order order = orderDao.findBy(Order.Column.order_uuid, orderCode);
		order.markAsActivated(Instant.now(), confirmerSeq);
		order.setOrderState(OrderState.ATV);
		if (fn != null) {
			fn.accept(order);
		}
		orderDao.activateOrder(order);
		
		/* licenses 발급 */
		AppConfiguration config = configService.getConfiguration();
		User teacher = userService.findTeacher(order.getCustomerRef());
		Product product = productService.findBy(Product.Column.prod_seq, order.getProductRef());
		
		Instant cur = Instant.now();
		List<License> items = licenseService.createLicenses( qttLicenses, order, (lcs) -> {
			lcs.setIssuerRef(config.getAdminAccountSeq());
			lcs.setReceiverRef(teacher.getSeq());
			lcs.setCreatedAt(cur);
			// 등록된 학생 상관없이 구매 후 바로 활성화시킴
			lcs.markAsActive(cur, product.getDurationInHours());
		});
		order.setItems(items);
		
		return order;
	}
	public List<Order> findOrdersWithProduct() {
		List<Order> orders = orderDao.findOrders();
		return orders;
	}
	/**
	 * 대기 중인 주문을 조회함(생성 후 결제 완료 및 취소 처리되지 않은 주문)
	 * @param orderUuid
	 * @return
	 */
	public Order findOrder(String orderUuid) {
		return orderDao.findBy(Order.Column.order_uuid, orderUuid);
	}
	/**
	 * 주문 상세 정보
	 * @param orderUuid
	 * @param option - {"product": boolean, license: boolean} 
	 * @return
	 */
	public Order findOrderDetail(String orderUuid, TypeMap option) {
		Boolean loadProduct = option.getBoolean("product");
		Boolean loadLicense = option.getBoolean("license");
		Order order = orderDao.findBy(Order.Column.order_uuid, orderUuid);
		if (loadProduct) {
			Product product = productService.findBy(Product.Column.prod_seq, order.getProductRef());
			order.bindProduct(product);
		}
		if (loadLicense) {
			List<License> licenses = licenseService.findsBy(License.Column.order_ref, order.getSeq());
			order.bindItems(licenses);
		}
		return order;
	}
	/**
	 * 진행중인 주문을 취소함(결제 완료된 주문을 취소하는 기능 아님). 주문이 READY상태일때만 가능
	 * @param orderUuid
	 * @return
	 */
	public Order cancelOrder(String orderUuid) {
		Order order = orderDao.findBy(Order.Column.order_uuid, orderUuid);
		if (!order.isPending()) {
			throw new AppException(ErrorCode.ORDER_ALREADY_ACTIVATED, 400);
		}
		order.cancelByUser();
		orderDao.updateState(order);
		return order;
	}

}
