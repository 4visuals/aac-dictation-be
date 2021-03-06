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
		String userId = studentInfo.getStr("userId");
		if (userId == null || userId.trim().length() == 0 ) {
			throw new AppException(ErrorCode.INVALID_VALUE, 400);
		}
		if (userService.exist(User.Column.user_id, userId)) {
			throw new AppException(ErrorCode.DUP_RESOURCE, 400, "userId: " + userId);
		}
		String password = studentInfo.getStr("pass");
		if (password == null || password.trim().length() == 0) {
			throw new AppException(ErrorCode.INVALID_VALUE, 400); 
		}
		LocalDate birth = studentInfo.getLocalDate("birth");
		User student = new User();
		student.setName(name);
		student.setUserId(userId);
		student.setBirth(birth);
		student.setTeacherRef(teacher.getSeq());
		student.setEmail(userId+"@aacdict");
		student.setCreationTime(Instant.now());
		student.setPass(password);
		student.setVendor(Vendor.MANUAL);
		student.setRole(UserRole.STUDENT);
		return studentDao.insertStudent(student);
	}
	/**
	 * ?????? ?????????
	 * ???????????? ???????????? ???????????? ????????? ?????????
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
		// ?????? ????????? ????????? ?????????!
		student.setPass(null); // ???????????? ??????
		
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
			 * ????????? ?????? ?????? ??????
			 * ?????? ???????????? ????????? ???????????? ?????????.
			 * ????????? ????????? ???????????? ???????????? ???????????? ??????
			 */
			throw new AppException(
				ErrorCode.LICENSE_PROBLEM, 400,
				"student is bound to the license, but teacher did not received it");
		}
		/*
		 * ????????? oauth???????????? ?????? ??????????????? ???????????? ????????????
		 * GoogleAuthService ??????
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
	/**
	 * ?????? ?????? ??????
	 * @param teacherSeq 
	 * @param form
	 * @return
	 */
	public User update(Long teacherSeq, User form) {
		User student = studentDao.findBy(User.Column.user_seq, form.getSeq());
		User teacher = userService.findTeacher(teacherSeq);
		if (!student.isStudentOf(teacher)) {
			throw new AppException(ErrorCode.NOT_YOUR_STUDENT, 403);
		}
		if (!student.isSameUserId(form) && userService.exist(User.Column.user_id, form.getUserId())) {
			throw new AppException(ErrorCode.DUP_RESOURCE, 409, "duplcated user id");
		}
		student.setName(form.getName());
		student.setBirth(form.getBirth());
		student.setUserId(form.getUserId());
		student.setPass(form.getPass());
		studentDao.updateStudent(student);
		return student;
	}
}
