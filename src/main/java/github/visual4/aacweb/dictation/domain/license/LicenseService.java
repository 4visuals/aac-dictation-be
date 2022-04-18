package github.visual4.aacweb.dictation.domain.license;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.order.Order;
import github.visual4.aacweb.dictation.domain.product.Product;
import github.visual4.aacweb.dictation.domain.user.User;
import github.visual4.aacweb.dictation.domain.user.UserService;

/**
 * 수강증 - 
 * @author chminseo
 *
 */
@Service
@Transactional
public class LicenseService {
	final LicenseDao licenseDao;
	
	public LicenseService(LicenseDao licenseDao) {
		this.licenseDao = licenseDao;
	}

	/**
	 * 라이센스 발급
	 * @param product
	 * @param cnt
	 * @return
	 */
	public List<License> createLicenses(
			Product product, int cnt, Order order, Consumer<License> setter) {
		Instant current = Instant.now();
		Integer orderRef = order == null ? null : order.getSeq();
		List<License> licenses  = new ArrayList<>();
		for(int i = 0 ; i < cnt ; i++) {
			License lcs = new License();
			lcs.setOrderRef(orderRef);
			lcs.setUuid("lcs-" + UUID.randomUUID().toString());
			lcs.setCreatedAt(current);
			setter.accept(lcs);
			licenseDao.insertLicense(lcs);
			licenses.add(lcs);
		}
		return licenses;
	}

	public List<License> findBy(License.Column column, Object value) {
		return licenseDao.findBy(column, value);
	}
	/**
	 * 수강권 등록 
	 * @param teacherSeq 선생님
	 * @param licenseSeq 수강권
	 * @param studentSeq 등록할 학생
	 * @return
	 */
	public TypeMap bindStudent(Long teacherSeq, Long licenseSeq, Long studentSeq,
			UserService userService) {
		User teacher = userService.findTeacher(teacherSeq);
		User student = userService.findStudent(studentSeq, (stud) -> stud.isStudentOf(teacher));
		if (student == null) {
			throw new AppException(ErrorCode.NOT_FOUND, 404, "no such student");
		}
		License lcs = licenseDao.findOneBy(License.Column.lcs_seq, licenseSeq);
		if (lcs == null) {
			throw new AppException(ErrorCode.NOT_FOUND, 404, "no such license");	
		}
		// FIXME (수강권) 이미 등록된 학생이 있는 경우 처리 로직 필요함.
		Long prevStudent = lcs.getStudentRef();
		licenseDao.bindStudent(lcs, student);
		
		return TypeMap.with(
				"prevStud", prevStudent,
				"curStud", student.getSeq(),
				"license", lcs.getSeq());
	}
}