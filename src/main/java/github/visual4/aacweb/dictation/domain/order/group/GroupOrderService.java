package github.visual4.aacweb.dictation.domain.order.group;

import java.time.Instant;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.order.group.GroupOrderForm.OrderFormState;
import github.visual4.aacweb.dictation.domain.user.User;
import github.visual4.aacweb.dictation.domain.user.UserService;
import github.visual4.aacweb.dictation.service.UuidService;
import github.visual4.aacweb.dictation.service.mailing.MailDto;
import github.visual4.aacweb.dictation.service.mailing.MailingService;

@Service
@Transactional
public class GroupOrderService {
	final String host;
	final String receiverMail;
	
	final GroupOrderDao groupOrderDao;
	final GroupPaperDao paperDao;
	final MailingService mailingService;
	final UuidService uuidService;
	final UserService userService;
	
	private final static String ORDER_URL = "@host/console";
	public GroupOrderService(
			@Value("${dictation.host}") String host,
			@Value("${dictation.ncp.mailing.receiver}") String receiverMail,
			GroupPaperDao paperDao,
			GroupOrderDao groupOrderDao,
			MailingService mailingService,
			UuidService uuidService, 
			UserService userService) {
		this.host = host;
		this.receiverMail = receiverMail;
		this.groupOrderDao = groupOrderDao;
		this.paperDao = paperDao;
		this.mailingService = mailingService;
		this.uuidService = uuidService;
		this.userService = userService;
	}
	/**
	 * 단체 구매 문의 등록
	 * @param teacherSeq 
	 * @param orderForm
	 * @return
	 */
	public GroupOrderForm createGroupOrderForm(Long teacherSeq, GroupOrderForm orderForm) {
		
		User teacher = userService.findTeacher(teacherSeq.longValue());
		if (teacher == null) {
			throw new AppException(ErrorCode.NOT_A_MEMBER, 400);
		}
		/* 1, 2, 3 순서 바꾸면 안됨*/
		
		/* 1 */
		orderForm.setCreationTime(Instant.now());
		orderForm.setState(OrderFormState.PND);
		orderForm.setSender(teacher);
		
		/* 2 */
		String uuid = uuidService.createUuid(orderForm);
		orderForm.setOrderUuid(uuid);
		
		/* 3 */
		String fullUrl = ORDER_URL.replace("@host", host);
		orderForm.setOrderFullUrl(fullUrl);
		
		escapeForm(orderForm);
		
		groupOrderDao.createGroupOrderForm(orderForm);
		for (OrderPaper paper : orderForm.getPapers()) {
			paper.fillDesc();
			paperDao.insertOrderPaper(orderForm.getSeq(), paper);
		}
		orderForm.setSender(teacher);
		/*
		 * 4. 관리자에게 메일 통보
		 */
//		MailDto mailToAdmin = new MailDto(
//				"[신규 문의] 단체 구매 " + orderForm.getSenderEmail(), 
//				"group-order-admin", 
//				orderForm, 
//				orderForm.getSenderEmail(), 
//				receiverMail, null, null);
//		mailingService.sendMail(mailToAdmin);
		/*
		 * 5. 신청자에게도 메일 통보
		 */
//		MailDto mailtoClient = new MailDto(
//				"[받아쓰기] 단체 구매 접수 완료",
//				"group-order-client",
//				orderForm,
//				receiverMail,
//				orderForm.getSenderEmail(),
//				null, null);
//		mailingService.sendMail(mailtoClient);
		return orderForm;
	}
	private void escapeForm(GroupOrderForm form) {
		form.setContent(StringEscapeUtils.escapeHtml4(form.getContent()));
		form.setOrgEmail(StringEscapeUtils.escapeHtml4(form.getOrgEmail()));
		form.setOrgName(StringEscapeUtils.escapeHtml4(form.getOrgName()));
		form.setSenderContactInfo(StringEscapeUtils.escapeHtml4(form.getSenderContactInfo()));
		form.setSenderEmail(StringEscapeUtils.escapeHtml4(form.getSenderEmail()));
		form.setSenderName(StringEscapeUtils.escapeHtml4(form.getSenderName()));
		
		for (OrderPaper paper : form.getPapers()) {
			paper.setDesc(StringEscapeUtils.escapeHtml4(paper.getDesc()));
		}
		
	}
	public List<GroupOrderForm> findOrders() {
		List<GroupOrderForm> orders = groupOrderDao.findOrders();
		for (GroupOrderForm order : orders) {
			escapeForm(order);
		}
		return orders;
	}
}
