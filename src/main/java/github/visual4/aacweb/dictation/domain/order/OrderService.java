package github.visual4.aacweb.dictation.domain.order;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.appconfig.AppConfigService;
import github.visual4.aacweb.dictation.domain.appconfig.AppConfiguration;
import github.visual4.aacweb.dictation.domain.license.License;
import github.visual4.aacweb.dictation.domain.license.LicenseService;
import github.visual4.aacweb.dictation.domain.order.Order.OrderState;
import github.visual4.aacweb.dictation.domain.order.aim_port.AimportDriver;
import github.visual4.aacweb.dictation.domain.order.group.GroupOrderForm;
import github.visual4.aacweb.dictation.domain.product.Product;
import github.visual4.aacweb.dictation.domain.product.ProductService;
import github.visual4.aacweb.dictation.domain.user.User;
import github.visual4.aacweb.dictation.domain.user.UserService;

@Service
@Transactional
public class OrderService {
	private static final Integer TRIAL_PRODUCT_SEQ = 2;
	final AppConfigService configService;
	final ProductService productService;
	final UserService userService;
	final LicenseService licenseService;
	final OrderDao orderDao;
	final AimportDriver portOneDriver;
	
	private static final Instant EXP2023 = Instant.parse("2024-01-01T00:00:00Z").minus(9, ChronoUnit.HOURS);
	private static final Set<OrderState> CANCEL_STATES = Set.of(
			OrderState.CNE,
			OrderState.CNR,
			OrderState.CNU);
	
	public OrderService(
			ProductService productService,
			AppConfigService configService,
			UserService userService, 
			LicenseService licenseService,
			OrderDao orderDao, 
			AimportDriver portOneDriver) {
		this.productService = productService;
		this.configService = configService;
		this.userService = userService;
		this.licenseService = licenseService;
		this.orderDao = orderDao;
		this.portOneDriver = portOneDriver;
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
		
		order.setTrialOrder(Boolean.TRUE);
		
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
	 * 소매 상품에 대한 새로운 주문 생성. 생성한 주문은 결제가 이루어지기 전까지 READY상태로 남아있음.
	 * @param teacherSeq
	 * @param productCode
	 * @param qttProduct - 라이선스 갯수(보통 1개)
	 * @return
	 */
	public Order createOrder(
			Long teacherSeq,
			String productCode,
			Integer qttProduct,
			Consumer<Order> hook) {
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
		
		order.setTrialOrder(Boolean.FALSE);
		
		order.setCustomerRef(teacher.getSeq());
		order.setConfirmerRef(null); // 관리자가 결제 승인할때 설정됨
		order.setOrderUuid("odr-" + UUID.randomUUID().toString());
		
		order.setOrderState(OrderState.RDY);
		order.setPaygateVendor(PG.im_port);
		order.setLicenseQtt(qttProduct);

		if(hook != null) {
			hook.accept(order);
		}
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
		Order order = createOrder(
				user.getSeq(), 
				trialProduct.getCode(), 1,
				(odr) -> odr.setTrialOrder(Boolean.TRUE));
		order= activateOrder(order.getOrderUuid(), 1L, 1, null);
		/*
		 * [이용권 무료 기간 연장] 12월 31일까지로
		 * https://github.com/4visuals/aac-writing/issues/143
		 */
		List<License> items = order.getItems();
		Instant expDate = Instant.now().plus(7, ChronoUnit.DAYS);
		for (License license : items) {
			licenseService.updateExpirationTime(license, expDate);
		}
		return order;
	}
	
	/**
	 * 주문을 승인함. 수강증 생성
	 * @param orderCode - order UUID
	 * @param confirmerSeq - 주문 확인 후 승인한 관리자
	 * @param qttLicenses - 라이선스 갯수
	 * @param fn
	 * @return
	 */
	public Order activateOrder(
			String orderCode,
			Long confirmerSeq,
			final Integer qttLicenses,
			Consumer<Order> fn) {
		Order order = orderDao.findOneBy(Order.Column.order_uuid, orderCode);
		order.markAsActivated(Instant.now(), confirmerSeq);
		order.setOrderState(OrderState.ATV);
		if (fn != null) {
			fn.accept(order);
		}
		orderDao.activateOrder(order);
		
		/* licenses 발급 */
		User teacher = userService.findTeacher(order.getCustomerRef());
		Product product = productService.findBy(Product.Column.prod_seq, order.getProductRef());
		
		Instant cur = Instant.now();
		AppConfiguration config = configService.getConfiguration();
		List<License> items = licenseService.createLicenses( qttLicenses, order, (lcs) -> {
			lcs.setIssuerRef(config.getAdminAccountSeq());
			lcs.setReceiverRef(teacher.getSeq());
			lcs.setCreatedAt(cur);
			// 등록된 학생 상관없이 구매 후 바로 활성화시킴
			lcs.markAsActive(cur, product.getDurationInHours());
			lcs.setTrialVersion(product.isTrialProduct());
		});
		order.setItems(items);
		
		return order;
	}
	/**
	 * 주문 내역(상품 정보 포함)
	 * @return
	 */
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
		return orderDao.findOneBy(Order.Column.order_uuid, orderUuid);
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
		Order order = orderDao.findOneBy(Order.Column.order_uuid, orderUuid);
		if (loadProduct) {
			Product product = pickProduct(order.getProductRef()); // productService.findBy(Product.Column.prod_seq, order.getProductRef());
			order.bindProduct(product);
		}
		if (loadLicense) {
			List<License> licenses = licenseService.findsBy(License.Column.order_ref, order.getSeq());
			order.bindItems(licenses);
		}
		return order;
	}
	private Product pickProduct(Integer productSeq) {
		// theme 정보를 얻기 위해서 findProducts()를 호출함
		List<Product> products = productService.findProducts();
		return products
				.stream()
				.filter(prd -> prd.getSeq().equals(productSeq))
				.findFirst()
				.orElse(null);
	}
	/**
	 * 결제 대기 중인 주문을 사용자가 취소함
	 * @param orderUuid
	 * @return
	 */
	public Order cancelPendingOrder(String orderUuid) {
		return cancelOrder(orderUuid, OrderState.RDY, OrderState.CNU);
	}
	/**
	 * 결제 완료된 주문을 취소시킴(전체 취소만 지원함)
	 * @param orderUuid
	 * @param reason - 취소 상태 코드
	 * @param updateRemote - 아임포트 api 호출 여부(kdict console에서 주문을 취소한 경우 원격 호출 필요함)
	 * @return
	 */
	public Order cancelActiveOrder(String orderUuid, OrderState reason, boolean updateRemote) {
		if (!CANCEL_STATES.contains(reason)) {
			String msg = String.format("expected %s, but %s", CANCEL_STATES.toString(), reason.name());
			throw new AppException(ErrorCode.ORDER_STATE_MISMATCH, 400, msg);
		}
		Order order = cancelOrder(orderUuid, OrderState.ATV, reason);
		
		if (updateRemote && !order.isGroupOrder()) {
			// FIXME 개별 구매인 경우 포트원 결제취소 api를 호출해야 함
			portOneDriver.cancelPayment(order.getOrderUuid());
		}
		return order;
	}
	/**
	 * 주문을 취소함
	 * @param orderUuid
	 * @param expectedStatus - 현재 주문 상태
	 * @param newStatus - 새로운 주문 상태. CNU(화면에서 결제 중에 창을 닫음),CNE(오류 발생),CNR(아임포트 관리자 화면에서 취소시킴)
	 * @return
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	private Order cancelOrder(String orderUuid, OrderState expectedStatus, OrderState newStatus) {
		Order order = orderDao.findOneBy(Order.Column.order_uuid, orderUuid);
		if (order.getOrderState() != expectedStatus) {
			throw new AppException(ErrorCode.ORDER_STATE_MISMATCH, 400,
					String.format("expected %s, but %s", expectedStatus.name(), order.getOrderState().name()));
		}
		
		order.setOrderState(newStatus);
		orderDao.updateState(order);
		return order;
	}
	/**
	 * 주어진 사용자의 모든 주문 내역 조회
	 * @param teacherSeq - 주문자
	 * @return
	 */
	public List<Order> findPurchasedOrders(Long teacherSeq) {
		return orderDao.findPurchasedOrders(teacherSeq);
	}
	/**
	 * 결제 시작 전 포트원에 검증 정보를 전송함
	 * @param teacherSeq
	 * @param orderUuid
	 */
	public void sendPaymentVerification(Long teacherSeq, String orderUuid) {
		Order order = orderDao.findOneBy(Order.Column.order_uuid, orderUuid);
		if (order.getOrderState() != OrderState.RDY) {
			throw new AppException(ErrorCode.ORDER_STATE_MISMATCH, 422, "NOT_A_READY");
		}
		if (!order.getCustomerRef().equals(teacherSeq)) {
			throw new AppException(ErrorCode.ORDER_INVALID_OWNER, 403, "NOT_A_OWNER");
		}
		portOneDriver.preparePayment(order.getOrderUuid(), order.getTotalAmount());
	}

}
