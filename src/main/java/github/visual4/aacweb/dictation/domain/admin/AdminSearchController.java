package github.visual4.aacweb.dictation.domain.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import github.visual4.aacweb.dictation.Res;
import github.visual4.aacweb.dictation.domain.license.License;
import github.visual4.aacweb.dictation.domain.license.LicenseService;
import github.visual4.aacweb.dictation.domain.student.StudentService;
import github.visual4.aacweb.dictation.domain.user.User;
import github.visual4.aacweb.dictation.domain.user.UserService;
/**
 * 관리자 검색 기능
 * 
 * @author chminseo
 *
 */
@RestController
@RequestMapping("/api/admin")
public class AdminSearchController {
	@Autowired
	UserService userService;
	@Autowired
	LicenseService licenseService;
	
	@GetMapping("/member")
	public Object searchMember(@RequestParam String keyword) {
		List<User> users = userService.searchUsers(keyword);
		return Res.success("members", users, "keyword", keyword);
	}
	
	@GetMapping("/member/{teacherSeq}/licenses")
	public Object findLicensesOfTeacher(@PathVariable Long teacherSeq) {
		List<License> licenses = licenseService.findsBy(License.Column.receiver_ref, teacherSeq);
		List<User> students = userService.findStudents(teacherSeq);
		return Res.success("licenses", licenses, "students", students);
	}
}
