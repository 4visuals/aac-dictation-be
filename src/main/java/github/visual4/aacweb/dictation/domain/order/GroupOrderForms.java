package github.visual4.aacweb.dictation.domain.order;

import org.apache.commons.text.StringEscapeUtils;

import github.visual4.aacweb.dictation.domain.order.group.GroupOrderForm;
import github.visual4.aacweb.dictation.domain.order.group.OrderPaper;

public class GroupOrderForms {

	/**
	 * XSS 방어
	 * @param form
	 */
	public static final void escape(GroupOrderForm form) {
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
	/**
	 * 공동 구매용 양식에 대해서만 원래 문자열로 변환함(content 내용이 json 형식이어야함)
	 * @param form
	 */
	public static final void unescape(GroupOrderForm form) {
		form.setContent(StringEscapeUtils.unescapeHtml4(form.getContent()));
		form.setOrgEmail(StringEscapeUtils.unescapeHtml4(form.getOrgEmail()));
		form.setOrgName(StringEscapeUtils.unescapeHtml4(form.getOrgName()));
		form.setSenderContactInfo(StringEscapeUtils.unescapeHtml4(form.getSenderContactInfo()));
		form.setSenderEmail(StringEscapeUtils.unescapeHtml4(form.getSenderEmail()));
		form.setSenderName(StringEscapeUtils.unescapeHtml4(form.getSenderName()));
		for (OrderPaper paper : form.getPapers()) {
			paper.setDesc(StringEscapeUtils.unescapeHtml4(paper.getDesc()));
		}
	}
}
