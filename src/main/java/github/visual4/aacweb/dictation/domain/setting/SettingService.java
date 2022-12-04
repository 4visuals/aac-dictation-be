package github.visual4.aacweb.dictation.domain.setting;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.domain.license.License;
import github.visual4.aacweb.dictation.domain.license.LicenseService;
import github.visual4.aacweb.dictation.domain.student.StudentService;
import github.visual4.aacweb.dictation.domain.user.User;
import github.visual4.aacweb.dictation.domain.user.UserService;

@Service
@Transactional
public class SettingService {

	final UserService userService;
	final StudentService studentService;
	final LicenseService licenseService;
	
	public SettingService(
			UserService userService,
			StudentService studentService,
			LicenseService licenseService) {
		this.userService = userService;
		this.studentService = studentService;
		this.licenseService = licenseService;
	}
	/**
	 * 비밀번호 변경을 위해서 현재 비밀 번호를 입력함
	 * @param password
	 */
	public void unlockPassword(Long teacherSeq, String password) {
		String currentPass = userService.findPassword(teacherSeq);
		if (!currentPass.equals(password)) {
			throw new AppException(ErrorCode.SETTING_UNLOCK_FAILED, 400);
		}
	}
	/**
	 * 비밀번호 변경
	 * @param teacherSeq
	 * @param password
	 */
	public void changePassword(Long teacherSeq, String newPass, String curPass) {
		if (!userService.updatePassword(teacherSeq, newPass, curPass)) {
			throw new AppException(ErrorCode.SERVER_ERROR, 500, "fail to update user password");
		}
	}
	/**
	 * 회원 탈퇴
	 * @param  userSeq - 탈퇴할 회원
	 */
	public void unsubscribe(Long userSeq) {
		User teacher = userService.findTeacher(userSeq);
		List<User> students = userService.findStudents(teacher.getSeq());
		List<License> licenses = licenseService.findsBy(License.Column.receiver_ref, teacher.getSeq());
		
		licenseService.deleteLicenses(licenses);
		studentService.deleteStudents(students, teacher);
		userService.deleteUser(teacher);
	}
}
