package github.visual4.aacweb.dictation;

public enum ProductSalesType {
	/**
	 * Retail - 소매 판매 상품
	 * 상품 구매 페이지에 모두 노출되는 기존 상품들
	 */
	RT,
	/**
	 * Group Buying - 공동구매용
	 * 공동 구매 결제를 위한 상품으로 사용자들로부터 수량과 가격을 확정받아서 직접 입력함
	 * 상품 구매 페이지에 노출시키지 않음 
	 */
	GB
}
