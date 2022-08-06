package github.visual4.aacweb.dictation.domain.stats;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import github.visual4.aacweb.dictation.Res;

@RestController
@RequestMapping("/api/stats")
public class ExamStatsController {
	final ExamStatsService statsService;
	
	public ExamStatsController(ExamStatsService statsService) {
		super();
		this.statsService = statsService;
	}
	/**
	 * 주어진 학생이 제출한 section 퀴즈 통계
	 * @return
	 */
	@GetMapping("/sections/student/{studentSeq}")
	public Object sectionStatsByStudent(@PathVariable Long studentSeq) {
		List<SectionStats> sectionStats = statsService.findSectionStatsByStudent(studentSeq);
		return Res.success("stats",  sectionStats);
	}
	/**
	 * 학생이 제출한 section 퀴즈 통계
	 * (주어진 수강증에 등록된 학생을 찾아서 조회)
	 * @param licenseUuid
	 * @return
	 */
	@GetMapping("/sections/license/{licenseUuid}")
	public Object sectionStatsByLicense(@PathVariable String licenseUuid) {
		List<SectionStats> sectionStats = statsService.findSectionStatsByLicense(licenseUuid);
		return Res.success("stats",  sectionStats);
	}
	/**
	 * 주어진 수강권으로 시도한 section의 퀴즈 통계
	 * @param sectionSeq - section PK
	 * @param license - 라이선스 id
	 * @return
	 */
	@GetMapping("/section/{sectionSeq}")
	public Object sectionStateByLicense(@PathVariable Integer sectionSeq, @RequestParam String license) {
		List<SectionStats> stats = statsService.findSectionStats(sectionSeq, license);
		return Res.success("stats", stats);
	}
}
