package github.visual4.aacweb.dictation.domain.order.group;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 단체 주문에서 선택한 증빙 서류를 나타냄
 * 현재 
 * @author chminseo
 *
 */
@Getter
@Setter
@ToString
public class OrderPaper {

	public enum PaperType {
		/**
		 * 견적서(estimate sheet)
		 */
		EST,
		/**
		 * 사업자등록증(cert of business registration)
		 */
		CBR,
		/**
		 * 통장 사본(bank account)
		 */
		BNK,
		/**
		 * 기타(직접입력)
		 */
		ETC
	}
	Integer formRef;
	PaperType paperType;
	String desc;
	
	public void fillDesc() {
		/*
		 * fronend에서 ETC(기타)인 경우에만 desc가 입력되어 있음.
		 * 여기서는  PaperType을 보고 desc를 채워넣음
		 */
		if(paperType == PaperType.EST) {
			desc = "견적서";
		} else if (paperType == PaperType.CBR) {
			desc = "사업자등록증";
		} else if (paperType == PaperType.BNK) {
			desc = "통장사본";
		}
		
	}
}
