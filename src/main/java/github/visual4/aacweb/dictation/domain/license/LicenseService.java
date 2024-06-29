package github.visual4.aacweb.dictation.domain.license;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
			int cnt, Order order, Consumer<License> setter) {
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

	public List<License> findsBy(License.Column column, Object value) {
		return licenseDao.findBy(column, value);
	}
	public TypeMap findLicensesWithOrder(Long teacherSeq) {
		List<License> licenses = licenseDao.findLicensesWithOrder(License.Column.receiver_ref, teacherSeq);
		Set<Order> orders = new HashSet<>();
		for (License lcs : licenses) {
			Order order = lcs.getOrder();
			if(order != null) {
				orders.add(order);
			}
			// 지워버림
			lcs.setOrder(null);
		}
		return TypeMap.with("licenses", licenses, "orders", orders);
	}
	public License findBy(License.Column column, Object value, boolean checkValidity) {
		List<License> licenses = findsBy(column, value);
		if (licenses.isEmpty()) {
			return null;
		}
		Instant now = Instant.now();
		List<License> alives = licenses.stream().filter((lcs) -> lcs.isAlive(now)).collect(Collectors.toList());
		if (alives.size() > 1) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500, String.format("expected 1 result, but %d, results", alives.size()));
		}
		if (alives.isEmpty()) {
			return null;
		}
		License license = alives.get(0);
		if (checkValidity && !license.isAlive(Instant.now())) {
			// 만료됨
			throw new AppException(ErrorCode.LICENSE_EXPIRED, 410, value);
		}
		return license;
	}
	public License findBy(License.Column column, Object value) {
		return findBy(column, value, false);
	}
	public TypeMap bindStudent(Long teacherSeq, String licenseUUID, Long studentSeq,
			UserService userService) {
		License lcs = this.findBy(License.Column.lcs_uuid, licenseUUID);
		if (lcs == null) {
			throw new AppException(ErrorCode.NOT_FOUND, 404, "no such license");	
		}
		return bindStudent(teacherSeq, lcs.getSeq(),studentSeq, userService);
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
//		
		licenseDao.bindStudent(lcs, student);
		
		return TypeMap.with(
				"prevStud", prevStudent,
				"curStud", student.getSeq(),
				"license", lcs.getSeq());
	}

	public void deleteLicenses(List<License> licenses) {
		for (License license : licenses) {
			licenseDao.deleteLicense(license);
		}
	}
	/**
	 * 만료 기간 변경
	 * 사용자가 단체 이용권을 재구매할때, 이용권 시작 시간을 나중으로 늦추려고 함(미리 구매한 경우)
	 * 
	 * @param license
	 * @param expTime
	 */
	public void updateExpirationTime(License license, Instant expTime) {
		license.setExpiredAt(expTime);
		licenseDao.updateExpirationTime(license);
	}
	/**
	 * 라이선스 사용자 변경함
	 * => 기존 선생님 A가 등록한 학생을 선생님 B에게 이동시킬 때, 선생님 B를 이용권의 사용자로 변경해야 함
	 * @param license
	 * @param nextTeacher
	 */
	public void changeReciever(String licenseUuid, User currentTeacher, User nextTeacher) {
		License lcs = this.findBy(License.Column.lcs_uuid, licenseUuid);
		
		lcs.setReceiverRef(nextTeacher.getSeq());
		
		licenseDao.changeReceiver(lcs);
		
	}
}
