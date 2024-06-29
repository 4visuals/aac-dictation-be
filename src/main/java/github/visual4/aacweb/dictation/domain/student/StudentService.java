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
import github.visual4.aacweb.dictation.service.rule.Rules;

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
		/*
		List<License> licenses =  licenseService.findsBy(License.Column.receiver_ref, teacher.getSeq());
		List<User> students = studentDao.findStudentsByTeacher(teacher.getSeq());
		if (students.size() >= licenses.size()) {
			throw new AppException(ErrorCode.LICENSE_IS_FULL, 422, 
					String.format("%d students of %d licenses", students.size(), licenses.size()));
		}
		*/
		Boolean randomProps = studentInfo.getBoolean("randomProps");
		randomProps = randomProps == null ? Boolean.FALSE : randomProps;
		
		String name = studentInfo.getStr("name");
		if (name == null || name.trim().length() == 0 ) {
			throw new AppException(ErrorCode.INVALID_VALUE, 400);
		}
		LocalDate birth = studentInfo.getLocalDate("birth");
		/*
		 * userId, password는 학생이 등록되는 수강증에 따라서 없을 수도 있음(x).
		 * 
		 * 학생 비번 다시 살림
		 * (XXXX) ref: https://github.com/4visuals/aac-writing/issues/105
		 * ([개별 구매 수강증] 아이디 비번 입력 막음)
		 * 
		 * 
		 * 
		 */
		String userId = studentInfo.getStr("userId");
		if (!randomProps && (userId == null || userId.trim().length() == 0) ) {
			throw new AppException(ErrorCode.INVALID_VALUE, 400);
		}
		if (userId == null) {
			userId = teacherSeq + "-" + Instant.now().toEpochMilli();
		}
		if (userService.exist(User.Column.user_id, userId)) {
			throw new AppException(ErrorCode.DUP_RESOURCE, 400, "userId: " + userId);
		}
		
		String password = studentInfo.getStr("pass");
		if (!randomProps && (password == null || password.trim().length() == 0)) {
			throw new AppException(ErrorCode.INVALID_VALUE, 400); 
		}
		if (password == null) {
			password = UUID.randomUUID().toString();
		}
		
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
	 * 학생 로그인
	 * 선생님이 만들어준 아이디와 비번을 사용함
	 * 
	 * @param studentId
	 * @param password
	 * @return
	 */
	public TypeMap login(String studentId, String password, boolean autoLogin) {
		User student = studentDao.findBy(User.Column.user_id, studentId);
		if (student == null) {
			System.out.println("no such student id: " + studentId);
			throw new AppException(ErrorCode.STUDENT_LOGIN_FAILED, 404);
		}
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
		return TypeMap.with(
				"membership", membership,
				"licenses", Arrays.asList(license),
				"student", student);
	}
	/**
	 * 학생 정보 수정
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
			throw new AppException(ErrorCode.DUP_USER_ID, 409, "duplcated user id");
		}
		String userId = form.getUserId().trim();
		if (userId.length() < 6 || userId.length() > 16) {
			throw new AppException(ErrorCode.OUT_OF_LENGTH, 409, "6~16");
		}
		userService.isValidateProperty("name", form.getName());
		userService.isValidateProperty("pass", form.getPass());
		student.setName(form.getName());
		student.setBirth(form.getBirth());
		student.setUserId(form.getUserId());
		student.setPass(form.getPass());
		studentDao.updateStudent(student);
		return student;
	}
	public User updateStudent(Long teacherSeq, Long studentSeq, String prop, Object value) {
		User student = studentDao.findBy(User.Column.user_seq, studentSeq);
		User teacher = userService.findTeacher(teacherSeq);
		if (!student.isStudentOf(teacher)) {
			throw new AppException(ErrorCode.NOT_YOUR_STUDENT, 403);
		}
		Object val = value;
		if ("userId".equals(prop)) {
			String userId = value.toString();
			if (!student.getUserId().equals(userId) && userService.exist(User.Column.user_id, userId)) {
				throw new AppException(ErrorCode.DUP_USER_ID, 409, "duplcated user id");
			}
			Rules.checkUserId(userId);
			student.setUserId(userId.trim());
		} else if ("name".equals(prop)) {
			val = userService.isValidateProperty("name", value.toString());
			student.setName(val.toString());
		} else if("pass".equals(prop)) {
			val = userService.isValidateProperty("pass", value);
			student.setPass(val.toString());
		} else if ("birth".equals(prop)) {
			val = userService.isValidateProperty("birth", value);
			System.out.println("birth");
			student.setBirth((LocalDate)val);
		} else {
			throw new AppException(ErrorCode.NOT_ALLOWED, 422);
		}
		studentDao.updateStudent(student);
		return student;
		
	}
	/**
	 * 학생들 삭제
	 * @param students
	 */
	public void deleteStudents(List<User> students, User teacher) {
		for (User student : students) {
			studentDao.deleteStudent(student.getSeq(), teacher.getSeq());
		}	
	}
	/**
	 * 주어진 학생 삭제
	 * @param teacherSeq
	 * @param studentSeq
	 * @return
	 */
	public User deleteStudent(Long teacherSeq, Long studentSeq) {
		User teacher = userService.findTeacher(teacherSeq);
		User student = studentDao.findBy(User.Column.user_seq, studentSeq);
		deleteStudents(Arrays.asList(student), teacher);
		return student;
	}
	public User changeTeacher(User student, Long newTeacherSeq) {
		User nextTeacher = userService.findTeacher(newTeacherSeq);
		if(nextTeacher == null) {
			//
		}
		student.setTeacherRef(newTeacherSeq);
		studentDao.changeTeacher(student);
		return student;
	}
}
