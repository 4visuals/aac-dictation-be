package github.visual4.aacweb.dictation.domain.stats;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.visual4.aacweb.dictation.domain.license.License;
import github.visual4.aacweb.dictation.domain.license.LicenseService;
import github.visual4.aacweb.dictation.domain.user.User;
import github.visual4.aacweb.dictation.domain.user.UserService;

@Service
@Transactional
public class ExamStatsService {

	final LicenseService licenseService;
	final ExamStatsDao statsDao;
	final UserService userSerivce;

	public ExamStatsService(LicenseService licenseService, ExamStatsDao statsDao, UserService userSerivce) {
		super();
		this.licenseService = licenseService;
		this.statsDao = statsDao;
		this.userSerivce = userSerivce;
	}

	public List<SectionStats> findSectionStatsByStudent(Long studentSeq) {
		return statsDao.findSectionStatsByStudent(studentSeq);
	}

	public List<SectionStats> findSectionStatsByLicense(String licenseUuid) {
		License license = licenseService.findBy(License.Column.lcs_uuid, licenseUuid, true);
		User student = userSerivce.findStudent(license.getStudentRef(), (stud) -> true);
		return findSectionStatsByStudent(student.getSeq());
	}
}
