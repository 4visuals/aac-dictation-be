package github.visual4.aacweb.dictation.domain.quotationform;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.YesNo;
import github.visual4.aacweb.dictation.domain.product.Product;
import github.visual4.aacweb.dictation.domain.product.ProductService;
import github.visual4.aacweb.dictation.domain.quotationform.dto.NewQuotationFormDto;
import github.visual4.aacweb.dictation.domain.user.User;
import github.visual4.aacweb.dictation.domain.user.UserService;
import github.visual4.aacweb.dictation.domain.order.Order;
import github.visual4.aacweb.dictation.domain.order.OrderService;
import github.visual4.aacweb.dictation.domain.order.PG;

@Service
@Transactional
public class QuotationFormService {

	final QuotationFormDao formDao;
	final QuotationFormItemDao itemDao;
	final ProductService productService;
	final UserService userService;
	final OrderService orderService;

	public QuotationFormService(
			QuotationFormDao formDao,
			QuotationFormItemDao itemDao,
			ProductService productService,
			UserService userService,
			OrderService orderService) {
		this.formDao = formDao;
		this.itemDao = itemDao;
		this.productService = productService;
		this.userService = userService;
		this.orderService = orderService;
	}

	public TypeMap createQuotationForm(NewQuotationFormDto dto) {
		if (dto == null) {
			throw new AppException(ErrorCode.INVALID_VALUE, 400);
		}
		if (dto.getItems() == null || dto.getItems().isEmpty()) {
			throw new AppException(ErrorCode.INVALID_VALUE2, 400, "items required");
		}

		QuotationForm form = mapForm(dto);
		List<QuotationFormItem> items = mapItems(dto.getItems());

		int totalAmount = 0;
		for (QuotationFormItem item : items) {
			totalAmount += item.getAmount();
		}
		if (form.getTotalAmount() == null) {
			form.setTotalAmount(totalAmount);
		} else if (!form.getTotalAmount().equals(totalAmount)) {
			throw new AppException(ErrorCode.VALUE_MISMATCH, 422, "totalAmount");
		}

		formDao.insertForm(form);
		for (QuotationFormItem item : items) {
			item.setFormRef(form.getSeq());
			itemDao.insertItem(item);
		}

		return TypeMap.with("form", form, "items", items);
	}

	public List<QuotationForm> findQuotationForms() {
		return formDao.findQuotationForms();
	}

	public TypeMap commitQuotationForm(Long quotationSeq, Long adminSeq) {
		if (quotationSeq == null) {
			throw new AppException(ErrorCode.INVALID_VALUE2, 400, "quotationSeq");
		}
		if (adminSeq == null) {
			throw new AppException(ErrorCode.INVALID_VALUE2, 400, "adminSeq");
		}
		User admin = userService.loadAdmin(adminSeq);
		QuotationForm form = formDao.findQuotationFormBySeq(quotationSeq);
		if (form == null) {
			throw new AppException(ErrorCode.NOT_FOUND, 404, "quotation");
		}
		if (form.getState() == QuotationForm.QuotationState.CLOSED) {
			throw new AppException(ErrorCode.NOT_ALLOWED, 422, "quotation closed");
		}
		if (form.getItems() == null || form.getItems().isEmpty()) {
			throw new AppException(ErrorCode.INVALID_VALUE2, 400, "items required");
		}
		String email = pickUserEmail(form);
		User teacher = userService.findTeacherByEmail(email);

		Instant activationTime = Instant.now();
		List<Order> orders = new ArrayList<>();
		for (QuotationFormItem item : form.getItems()) {
			orders.add(issueOrderByItem(teacher, item, admin.getSeq(), form.getSeq(), activationTime));
		}
		closeQuotationFormAsIssued(form);
		return TypeMap.with("orders", orders, "form", form);
	}

	public TypeMap updateQuotationForm(QuotationForm form) {
		if (form == null || form.getSeq() == null) {
			throw new AppException(ErrorCode.INVALID_VALUE2, 400, "seq");
		}
		if (form.getItems() == null || form.getItems().isEmpty()) {
			throw new AppException(ErrorCode.INVALID_VALUE2, 400, "items required");
		}
		normalizeForm(form);
		List<QuotationFormItem> items = normalizeItems(form.getItems());

		int totalAmount = 0;
		for (QuotationFormItem item : items) {
			totalAmount += item.getAmount();
		}
		if (form.getTotalAmount() == null) {
			form.setTotalAmount(totalAmount);
		} else if (!form.getTotalAmount().equals(totalAmount)) {
			throw new AppException(ErrorCode.VALUE_MISMATCH, 422, "totalAmount");
		}

		formDao.updateForm(form);
		itemDao.deleteItemsByFormRef(form.getSeq());
		for (QuotationFormItem item : items) {
			item.setFormRef(form.getSeq());
			itemDao.insertItem(item);
		}
		form.setItems(items);
		return TypeMap.with("form", form, "items", items);
	}

	public QuotationForm closeQuotationForm(Long seq) {
		if (seq == null) {
			throw new AppException(ErrorCode.INVALID_VALUE2, 400, "seq");
		}
		QuotationForm form = new QuotationForm();
		form.setSeq(seq);
		form.setState(QuotationForm.QuotationState.CLOSED);
		form.setCloseReason(QuotationForm.CloseReason.STOPPED);
		form.setClosedAt(Instant.now());
		formDao.closeForm(form);
		return form;
	}

	private QuotationForm mapForm(NewQuotationFormDto formDto) {
		String origin = formDto.getOrigin();
		if (origin == null || origin.trim().isEmpty()) {
			origin = "s2b";
		}
		QuotationForm form = new QuotationForm();
		form.setOrigin(origin);
		form.setIssueDate(formDto.getIssueDate());
		form.setOrgName(formDto.getOrgName());
		form.setAdminName(formDto.getAdminName());
		form.setAdminEmail(formDto.getAdminEmail());
		form.setAdminPhone(formDto.getAdminPhone());
		form.setGreamEmail(formDto.getGreamEmail());
		form.setRequesterName(formDto.getRequesterName());
		form.setRequesterEmail(formDto.getRequesterEmail());
		form.setRequesterPhone(formDto.getRequesterPhone());
		form.setTotalAmount(formDto.getTotalAmount());
		return form;
	}

	private List<QuotationFormItem> mapItems(List<NewQuotationFormDto.ItemDto> items) {
		List<QuotationFormItem> mapped = new ArrayList<>(items.size());
		for (NewQuotationFormDto.ItemDto itemDto : items) {
			if (itemDto.getProductCode() == null || itemDto.getProductCode().trim().isEmpty()) {
				throw new AppException(ErrorCode.INVALID_VALUE2, 400, "productCode required");
			}
			if (itemDto.getQty() == null || itemDto.getQty() <= 0) {
				throw new AppException(ErrorCode.INVALID_VALUE2, 400, "qty required");
			}
			if (itemDto.getUnitPrice() == null || itemDto.getUnitPrice() < 0) {
				throw new AppException(ErrorCode.INVALID_VALUE2, 400, "unitPrice required");
			}

			Product product = productService.findBy(Product.Column.prod_code, itemDto.getProductCode());
			if (product == null) {
				throw new AppException(ErrorCode.ORDER_INVALID_PROD, 422, itemDto.getProductCode());
			}

			Integer amount = itemDto.getUnitPrice() * itemDto.getQty();
			if (itemDto.getAmount() != null && !itemDto.getAmount().equals(amount)) {
				throw new AppException(ErrorCode.VALUE_MISMATCH, 422, "amount");
			}

			QuotationFormItem item = new QuotationFormItem();
			item.setProductRef(product.getSeq());
			item.setProductName(product.getName());
			item.setProductCode(product.getCode());
			item.setQty(itemDto.getQty());
			item.setUnitPrice(itemDto.getUnitPrice());
			item.setAmount(amount);
			mapped.add(item);
		}
		return mapped;
	}

	private void normalizeForm(QuotationForm form) {
		if (form.getOrigin() == null || form.getOrigin().trim().isEmpty()) {
			form.setOrigin("s2b");
		}
		if (form.getState() == null) {
			form.setState(QuotationForm.QuotationState.OPEN);
		}
	}

	private List<QuotationFormItem> normalizeItems(List<QuotationFormItem> items) {
		List<QuotationFormItem> mapped = new ArrayList<>(items.size());
		for (QuotationFormItem item : items) {
			if (item.getQty() == null || item.getQty() <= 0) {
				throw new AppException(ErrorCode.INVALID_VALUE2, 400, "qty required");
			}
			if (item.getUnitPrice() == null || item.getUnitPrice() < 0) {
				throw new AppException(ErrorCode.INVALID_VALUE2, 400, "unitPrice required");
			}

			Product product = null;
			if (item.getProductRef() != null) {
				product = productService.findBy(Product.Column.prod_seq, item.getProductRef());
			}
			if (product == null && item.getProductCode() != null && !item.getProductCode().trim().isEmpty()) {
				product = productService.findBy(Product.Column.prod_code, item.getProductCode());
			}
			if (product == null) {
				throw new AppException(ErrorCode.ORDER_INVALID_PROD, 422,
						item.getProductCode() != null ? item.getProductCode() : String.valueOf(item.getProductRef()));
			}

			item.setProductRef(product.getSeq());
			if (item.getProductName() == null || item.getProductName().trim().isEmpty()) {
				item.setProductName(product.getName());
			}
			item.setProductCode(product.getCode());

			Integer amount = item.getUnitPrice() * item.getQty();
			if (item.getAmount() != null && !item.getAmount().equals(amount)) {
				throw new AppException(ErrorCode.VALUE_MISMATCH, 422, "amount");
			}
			item.setAmount(amount);
			mapped.add(item);
		}
		return mapped;
	}

	private String pickUserEmail(QuotationForm form) {
		String email = form.getGreamEmail();
		if (email == null || email.isBlank()) {
			email = form.getRequesterEmail();
		}
		if (email == null || email.isBlank()) {
			throw new AppException(ErrorCode.INVALID_VALUE2, 400, "email");
		}
		return email.trim();
	}

	private Order issueOrderByItem(User teacher, QuotationFormItem item, Long adminSeq, Long quotationSeq,
			Instant activationTime) {
		if (item.getQty() == null || item.getQty() <= 0) {
			throw new AppException(ErrorCode.INVALID_VALUE2, 400, "qty required");
		}
		if (item.getUnitPrice() == null || item.getUnitPrice() < 0) {
			throw new AppException(ErrorCode.INVALID_VALUE2, 400, "unitPrice required");
		}
		Product product = null;
		if (item.getProductRef() != null) {
			product = productService.findBy(Product.Column.prod_seq, item.getProductRef());
		}
		if (product == null && item.getProductCode() != null && !item.getProductCode().trim().isEmpty()) {
			product = productService.findBy(Product.Column.prod_code, item.getProductCode());
		}
		if (product == null) {
			throw new AppException(ErrorCode.ORDER_INVALID_PROD, 422,
					item.getProductCode() != null ? item.getProductCode() : String.valueOf(item.getProductRef()));
		}

		Integer amount = item.getUnitPrice() * item.getQty();
		if (item.getAmount() != null && !item.getAmount().equals(amount)) {
			throw new AppException(ErrorCode.VALUE_MISMATCH, 422, "amount");
		}
		item.setAmount(amount);

		int licensePerProduct = product.getLicenseQtt() == null ? 1 : product.getLicenseQtt();
		final int licenseQtt = product.getDigitalType() == YesNo.N
				? 0
				: item.getQty() * licensePerProduct;

		Order order = orderService.createOrder(
				teacher.getSeq(),
				product.getCode(),
				item.getQty(),
				odr -> {
					odr.setTotalAmount(amount);
					odr.setLicenseQtt(licenseQtt);
					odr.setPaygateVendor(PG.s2b);
				});
		return orderService.activateOrder(order.getOrderUuid(), adminSeq, licenseQtt, activationTime, odr -> {
			odr.setTransactionDetail("{\"quotation\": " + quotationSeq + "}");
			odr.setMidTransactionUid("quotation-" + quotationSeq);
		});
	}

	private void closeQuotationFormAsIssued(QuotationForm form) {
		form.setState(QuotationForm.QuotationState.CLOSED);
		form.setCloseReason(QuotationForm.CloseReason.ISSUED);
		form.setClosedAt(Instant.now());
		formDao.closeForm(form);
	}
}
