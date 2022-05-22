package github.visual4.aacweb.dictation.domain.student;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.license.License;
import github.visual4.aacweb.dictation.domain.license.LicenseDao;
import github.visual4.aacweb.dictation.domain.license.LicenseService;
import github.visual4.aacweb.dictation.domain.user.Membership;
import github.visual4.aacweb.dictation.domain.user.User;
import github.visual4.aacweb.dictation.domain.user.UserRole;
import github.visual4.aacweb.dictation.domain.user.UserService;
import github.visual4.aacweb.dictation.domain.user.Vendor;

@Service
@Transactional
public class StudentService {
	
	final UserService userService;
	final StudentDao studentDao;
//	final LicenseDao licenseDao;
	final LicenseService licenseService;
	public StudentService(UserService userService, StudentDao studentDao, LicenseService licenseService) {
		this.userService = userService;
		this.studentDao = studentDao;
//		this.licenseDao = licenseDao;
		this.licenseService = licenseService;
	}
	public User regiserStudent(Long teacherSeq, TypeMap studentInfo) {
		User teacher = userService.findTeacher(teacherSeq);
		List<License> licenses =  licenseService.findsBy(License.Column.receiver_ref, teacher.getSeq());
		List<User> students = studentDao.findStudentsByTeacher(teacher.getSeq());
		if (students.size() >= licenses.size()) {
			throw new AppException(ErrorCode.LICENSE_IS_FULL, 422, 
					String.format("%d students of %d licenses", students.size(), licenses.size()));
		}
		
		String name = studentInfo.getStr("name");
		if (name == null || name.trim().length() == 0 ) {
			throw new AppException(ErrorCode.INVALID_VALUE, 400);
		}
		String password = studentInfo.getStr("password");
		if (password == null || password.trim().length() == 0) {
			password = UUID.randomUUID().toString(); 
		}
		LocalDate birth = studentInfo.getLocalDate("birth");
		User student = new User();
		student.setName(name);
		student.setBirth(birth);
		student.setTeacherRef(teacher.getSeq());
		student.setEmail("");
		student.setCreationTime(Instant.now());
		student.setPass(password);
		student.setVendor(Vendor.MANUAL);
		student.setRole(UserRole.STUDENT);
		return studentDao.insertStudent(student);
	}
	/**
	 * 학생 로그인
	 * 선생님이 만들어준 아이디와 비번을 사용함
	 * 
	 * @param studentId
	 * @param password
	 * @return
	 */
	public TypeMap login(String studentId, String password, boolean autoLogin) {
		User student = studentDao.findBy(User.Column.user_id, studentId);
		if (!autoLogin && !student.getPass().equals(password)) {
			throw new AppException(ErrorCode.STUDENT_LOGIN_FAILED, 403);
		}
		// 아래 코드를 지우지 마시오!
		student.setPass(null); // 패스워드 뭉갬
		
		User teacher = userService.findTeacher(student.getTeacherRef());
		if (!student.isStudentOf(teacher)) {
			throw new AppException(ErrorCode.NOT_A_STUDENT, 409);
		}
		
		student.setTeacher(teacher);
		
		License license = licenseService.findBy(License.Column.student_ref, student.getSeq(), true);
		if (license == null) {
			throw new AppException(ErrorCode.STUDENT_NOT_ENROLLED, 409, studentId);
		}
		if(!license.isValidReceiver(teacher)) {
			/*
			 * 여기에 걸릴 일이 없음
			 * 특정 학생에게 연결된 수강증은 존재함.
			 * 그런데 학생의 선생님이 수강증의 사용자가 아님
			 */
			throw new AppException(
				ErrorCode.LICENSE_PROBLEM, 400,
				"student is bound to the license, but teacher did not received it");
		}
		/*
		 * 학생은 oauth로그인을 하지 않기때문에 비슷하게 만들어줌
		 * GoogleAuthService 참고
		 */
		TypeMap studentProfile = TypeMap.with(
				"vendor", student.getVendor().name(),
				"name", student.getName(),
				"email", student.getEmail(),
				"verified", true,
				"picture", null,
				"aac_id", student.getUserId());
		Membership membership = new Membership(student, studentProfile, Vendor.MANUAL.name());
		return TypeMap.with("membership", membership, "licenses", Arrays.asList(license));
	}
}
