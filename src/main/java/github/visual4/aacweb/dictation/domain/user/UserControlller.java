package github.visual4.aacweb.dictation.domain.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.visual4.aacweb.dictation.Res;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.student.StudentService;
import github.visual4.aacweb.dictation.service.TokenService;
import github.visual4.aacweb.dictation.web.aop.JwtProp;

@RestController
@RequestMapping("/api/user")
public class UserControlller {

	@Autowired
	UserService userService;
	
	@Autowired
	StudentService studentService;
	
	@Autowired
	TokenService tokenService;
	/**
	 * oauth 로그인 후 얻어낸 token으로 사용자 프로필 확인 후 토큰 발행
	 * @param params
	 * @return
	 */
	@PostMapping("/membership")
	public Object checkMembership(@RequestBody TypeMap params) {
		String vendor = params.getStr("vendor");
		String type = params.getStr("type");
		String accessToken = params.getStr("token"); // access token from google oauth
		TypeMap res = userService.getMembership(vendor, accessToken);
		
		String jwtToken = tokenService.generateJwt(
				res.<Membership>get("membership").getProfile(),
				UserRole.TEACHER);
		res.put("jwt", jwtToken);
		return Res.success(res);
	}
	/**
	 * auto login
	 * @param payload
	 * @return
	 */
	@PostMapping("/login")
	public Object login(@JwtProp TypeMap payload) {
		UserRole role = payload.get("role");
		TypeMap res = null;
		if (role == UserRole.TEACHER) {
			res = userService.login(payload);
		} else if (role == UserRole.STUDENT) {
			String studentId = payload.getStr("aac_id");
			res = studentService.login(studentId, null, true);
		}
//		UserRole role = isTeacher ? UserRole.TEACHER : UserRole.STUDENT;
		String jwtToken = tokenService.generateJwt(
				res.<Membership>get("membership").getProfile(), role);
		res.put("jwt", jwtToken);
		return Res.success(res);
	}
	
	@PostMapping("/join")
	public Object join(@JwtProp TypeMap profile) {
		TypeMap res = userService.join(profile);
		String jwtToken = tokenService.generateJwt(
				res.<Membership>get("membership").getProfile(), UserRole.TEACHER);
		res.put("jwt", jwtToken);
		return Res.success(res);
	}
}
