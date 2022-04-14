package github.visual4.aacweb.dictation.domain.student;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.user.User;
import github.visual4.aacweb.dictation.domain.user.UserRole;
import github.visual4.aacweb.dictation.domain.user.UserService;
import github.visual4.aacweb.dictation.domain.user.Vendor;

@Service
@Transactional
public class StudentService {
	
	final UserService userService;
	final StudentDao studentDao;
	public StudentService(UserService userService, StudentDao studentDao) {
		this.userService = userService;
		this.studentDao = studentDao;
	}
	public User regiserStudent(Long teacherSeq, TypeMap studentInfo) {
		User teacher = userService.findTeacher(teacherSeq);
		String name = studentInfo.getStr("name");
		LocalDate birth = studentInfo.getLocalDate("birth");
		User student = new User();
		student.setName(name);
		student.setBirth(birth);
		student.setTeacherRef(teacher.getSeq());
		student.setEmail("");
		student.setCreationTime(Instant.now());
		student.setPass(UUID.randomUUID().toString());
		student.setVendor(Vendor.MANUAL);
		student.setRole(UserRole.STUDENT);
		return studentDao.insertStudent(student);
	}
}
