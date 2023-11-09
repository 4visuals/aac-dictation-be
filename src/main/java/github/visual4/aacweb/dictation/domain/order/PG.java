package github.visual4.aacweb.dictation.domain.order;
/**
 * paygate
 * @author chminseo
 *
 */
public enum PG {
	im_port,
	/**
	 * import에서 토스페이먼츠를 사용할 경우(구모듈, LGU+ 로 결제됨)
	 * https://guide.portone.io/01cd8f06-6952-493c-8b77-9c4f235c464c
	 */
	uplus,
	/**
	 * 단체 구매는 별도의 연락을 통해서 금액을 확정 후 입금함.
	 * 입금 확인 후 관리자 화면에서 수강증을 발급함
	 * 
	 *  @see GroupOrderService.commit
	 */
	group_order,
	danal_tpay
}
