package github.visual4.aacweb.dictation.domain.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.visual4.aacweb.dictation.Res;
import github.visual4.aacweb.dictation.domain.user.UserService;
import github.visual4.aacweb.dictation.web.aop.JwtProp;

@RestController
@RequestMapping("/api/admin/student")
public class AdminStudentController {

	@Autowired
	UserService userService;
	
	@Autowired
	AdminService adminService;
	
	@PutMapping("/{studentSeq}")
	public Object transferStudent(
			@JwtProp("useq") Integer userSeq,
			@PathVariable Integer studentSeq,
			@RequestBody StudentTransferDto dto) {
		userService.loadAdmin(userSeq.longValue());
		adminService.transforStudent(dto);
		return Res.success("dto", dto);
	}
}
