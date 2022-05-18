package github.visual4.aacweb.dictation.domain.student;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.license.License;
import github.visual4.aacweb.dictation.domain.license.LicenseDao;
import github.visual4.aacweb.dictation.domain.user.User;
import github.visual4.aacweb.dictation.domain.user.UserRole;
import github.visual4.aacweb.dictation.domain.user.UserService;
import github.visual4.aacweb.dictation.domain.user.Vendor;

@Service
@Transactional
public class StudentService {
	
	final UserService userService;
	final StudentDao studentDao;
	final LicenseDao licenseDao;
	public StudentService(UserService userService, StudentDao studentDao, LicenseDao licenseDao) {
		this.userService = userService;
		this.studentDao = studentDao;
		this.licenseDao = licenseDao;
	}
	public User regiserStudent(Long teacherSeq, TypeMap studentInfo) {
		User teacher = userService.findTeacher(teacherSeq);
		List<License> licenses = licenseDao.findBy(License.Column.receiver_ref, teacher.getSeq());
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
}
