package github.visual4.aacweb.dictation.domain.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.visual4.aacweb.dictation.Res;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.license.License;
import github.visual4.aacweb.dictation.domain.order.Order;
import github.visual4.aacweb.dictation.domain.order.OrderService;
import github.visual4.aacweb.dictation.domain.section.Section;
import github.visual4.aacweb.dictation.domain.section.SectionService;
import github.visual4.aacweb.dictation.domain.sentence.Sentence;
import github.visual4.aacweb.dictation.domain.sentence.SentenceService;
import github.visual4.aacweb.dictation.korean.Mark;
import github.visual4.aacweb.dictation.service.analysis.AnalysisService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
	@Autowired
	AnalysisService anaService;
	@Autowired
	SentenceService sentenceService;
	@Autowired
	SectionService sectionService;
	@Autowired
	AdminService adminService;
	@Autowired
	OrderService orderService;
	
	/**
	 * 주어진 문장의 level 정보 분석
	 * @param sentenceSeq
	 * @return
	 */
	@GetMapping("/sentence/{sentenceSeq}/levels")
	public Object parseLevels(@PathVariable Integer sentenceSeq) {
		Sentence sen = sentenceService.findBySeq(sentenceSeq);
		Mark mark = anaService.parseDifficulties(sen.getSentence());
		return Res.success("sentence", sen, "levels", mark.toList());
	}
	/**
	 * 주어진 section의 모든 문장, 단어의 난이도 조회
	 * @return
	 */
	@GetMapping("/section/{sectionSeq}/difficulties")
	public Object parseDifficulties(@PathVariable Integer sectionSeq) {
		Section section = sectionService.findBy(Section.Column.seq, sectionSeq);
		TypeMap dfMap = anaService.parseDifficulties(section);
		return Res.success("df", dfMap);
	}
	/**
	 * 라이선스 신규 발급
	 * 관리자가 특정 사용자에게 라이선스를 발급함
	 * @return
	 */
	@PostMapping("/license")
	public Object createLicense(@RequestBody TypeMap param) {
		Long userSeq = param.asLong("userSeq"); // 선생님
		Integer qtt = param.getInt("qtt"); // 갯수
		List<License> licenses = adminService.issueLicenses(userSeq, qtt);
		return Res.success("licenses", licenses);
	}
	
	@GetMapping("/orders")
	public Object listOrders() {
		List<Order> orders = orderService.findOrdersWithProduct();
		return Res.success("orders", orders);
	}
}
