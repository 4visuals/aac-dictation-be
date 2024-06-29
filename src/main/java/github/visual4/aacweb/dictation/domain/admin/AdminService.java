package github.visual4.aacweb.dictation.domain.admin;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.Util;
import github.visual4.aacweb.dictation.domain.license.License;
import github.visual4.aacweb.dictation.domain.license.LicenseService;
import github.visual4.aacweb.dictation.domain.product.Product;
import github.visual4.aacweb.dictation.domain.product.ProductService;
import github.visual4.aacweb.dictation.domain.student.StudentService;
import github.visual4.aacweb.dictation.domain.user.User;
import github.visual4.aacweb.dictation.domain.user.UserService;

@Service
@Transactional
public class AdminService {
	final ProductService productService;
	final LicenseService licenseService;
	final UserService userService;
	final StudentService studentService;
	final Long adminSeq;
	public AdminService(
			ProductService productService,
			LicenseService licenseService,
			UserService userService,
			StudentService studentService,
			@Value("${dictation.admin.seq}") Long adminSeq) {
		this.productService = productService;
		this.licenseService = licenseService;
		this.userService = userService;
		this.studentService = studentService;
		this.adminSeq = adminSeq;
	}
	/**
	 * 관리자가 직접 수강증 신규 발급
	 * @param userSeq
	 * @param qtt
	 * @return 
	 */
	public List<License> issueLicenses(Long userSeq, Integer qtt) {
		User adminAccount = userService.findUser(adminSeq);
		Instant now = Instant.now();
		return licenseService.createLicenses(qtt, null, (lcs)-> {
			lcs.setIssuerRef(adminAccount.getSeq());
			lcs.setReceiverRef(userSeq);
			lcs.markAsActive(now, 24*28); // 28일
		});
	}
	
	
	/**
	 * 학생의 선생님 변경
	 * 
	 * @see https://github.com/4visuals/aac-writing/issues/161
	 * 
	 * @param dto
	 */
	public void transforStudent(StudentTransferDto dto) {
		User currentTeacher = userService.findTeacher(dto.teacher);
		if(currentTeacher == null) {
			//
		}
		
		User student = userService.findStudent(dto.studentSeq, stud -> stud.isStudentOf(currentTeacher));
		if(student == null) {
			// currentTeachr의 학생이 아님
		}
		User nextTeacher = userService.findTeacher(dto.nextTeacher);
		if (currentTeacher.equals(nextTeacher)) {
			// 자기 자신
		}
		
		License license = licenseService.findBy(License.Column.lcs_uuid, dto.license, true);
		if(license == null) {
			// license 가 없는 경우
			// 만료이면 오류 던짐
		}
		
		// 선생님 변경
		studentService.changeTeacher(student, dto.nextTeacher);
		// 라이선스 사용자 변경
		license.setReceiverRef(nextTeacher.getSeq());
		licenseService.changeReciever(license.getUuid(), currentTeacher, nextTeacher);
		
	}
}
