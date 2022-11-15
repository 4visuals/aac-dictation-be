package github.visual4.aacweb.dictation.domain.license;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.visual4.aacweb.dictation.Res;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.user.UserService;
import github.visual4.aacweb.dictation.web.aop.JwtProp;

@RestController
@RequestMapping("/api")
public class LicenseController {

	@Autowired
	LicenseService licenseService;
	@Autowired
	UserService userService;
	
	@PutMapping("/license/{licenseSeq}/student/{studentSeq}")
	public Object bindStudent(
			@PathVariable Integer licenseSeq, 
			@PathVariable Integer studentSeq,
			@JwtProp("useq") Integer teacherSeq) {
		TypeMap res  = licenseService.bindStudent(
				teacherSeq.longValue(),
				licenseSeq.longValue(),
				studentSeq.longValue(),
				userService);
		return Res.success(res);
	}
}
