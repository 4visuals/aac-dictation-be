package github.visual4.aacweb.dictation.domain.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.visual4.aacweb.dictation.Res;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.exam.ExamService;
import github.visual4.aacweb.dictation.domain.exam.recent.RecentPaper;
import github.visual4.aacweb.dictation.domain.exam.recent.RecentPaperService;
import github.visual4.aacweb.dictation.domain.license.License;
import github.visual4.aacweb.dictation.domain.license.LicenseService;
import github.visual4.aacweb.dictation.domain.order.Order;
import github.visual4.aacweb.dictation.domain.order.OrderService;
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
	
	@Autowired
	ExamService examService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	RecentPaperService recentPaperService;
	
	@Autowired
	LicenseService licenseService;
	
	/**
	 * oauth 로그인 후 얻어낸 token으로 사용자 프로필 확인 후 토큰 발행
	 * @param params
	 * @return
	 */
	@PostMapping("/membership")
	public Object checkMembership(@RequestBody TypeMap params) {
		String vendor = params.getStr("vendor");
		String type = params.getStr("type");
		
		String token = params.getStr("token"); // access token from google oauth
		TypeMap res = null;
		if ("id_token".equals(type)) {
			res = userService.getMembershipFromIdToken(vendor, token);
		} else if ("access_token".equals(type)) {
			res = userService.getMembership(vendor, token);
		}
		checkTrialLicense(res);
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
	public Object login(@JwtProp TypeMap payload, @RequestBody TypeMap params) {
		UserRole role = payload.get("role");
		TypeMap res = null;
		if (role == UserRole.TEACHER) {
			res = userService.login(payload);
			System.out.println("[auto login] " + params);
			checkTrialLicense(res);
			String licenseUuid = params.getStr("license");
			if (licenseUuid != null) {
				/*
				 * 페이지 새로 고침 후 자동 로그인 시
				 * 학생의 최근 시험 기록을 같이 첨부합니다.
				  */
				Long teacherSeq = payload.asLong("useq");
				License license = licenseService.findBy(License.Column.lcs_uuid, licenseUuid);
				
				User student = userService.findStudent(license.getStudentRef(), (std) -> true);
				List<RecentPaper> records = recentPaperService.findWrongAnswersByStudent(teacherSeq, student.getSeq());
				res.put("records", records);
			}
		} else if (role == UserRole.STUDENT) {
			/*
			 * FIXME 학생이 로그인했을때 record 첨부 기능 필요함.
			 */
			String studentId = payload.getStr("aac_id");
			res = studentService.login(studentId, null, true);
			/*
			 * 학생인 경우 segment별 최근 시험 이력을 같이 보냄
			 * 성취도 표시에 사용됨
			 */
			List<License> licenses = res.get("licenses");
			TypeMap exams = examService.queryBySectionChunk(licenses.get(0).getUuid());
			res.put("segments", exams.get("quiz"));
		}
//		UserRole role = isTeacher ? UserRole.TEACHER : UserRole.STUDENT;
		String jwtToken = tokenService.generateJwt(
				res.<Membership>get("membership").getProfile(), role);
		res.put("jwt", jwtToken);
		return Res.success(res);
	}
	/**
	 * id, password로 로그인. 결제 대행 업체 테스트용 로그인 처리를 위해서 추가함
	 * @param payload
	 * @return
	 */
	@PostMapping("/login/manual")
	public Object loginManullay(@RequestBody TypeMap payload) {
		UserRole role = UserRole.TEACHER;
		payload.put("role", role);
		TypeMap res = userService.loginManually(payload);
		checkTrialLicense(res);
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
	@PostMapping("/join/manual")
	public Object joinManually(@RequestBody JoinDto joinForm) {
		User user = userService.joinManually(joinForm);
		return Res.success("user", user);
	}
	@PostMapping("/join/prop")
	public Object checkPropertyForJoin(@RequestBody TypeMap param) {
		String column = param.getStr("prop");
		Object value = param.get("value");
		Object val = userService.isValidateProperty(column, value);
		return Res.success("value", val);
	}
	
	@PostMapping("/prop")
	public Object checkUserProperty(
			@JwtProp TypeMap profile, @RequestBody TypeMap param) {
		String column = param.getStr("prop");
		Object value = param.get("value");
		Object val = userService.isValidateProperty(column, value);
		return Res.success("value", val);
	}
	/**
	 * 발급된 라이선스가 하나도 없으면 무료 평가판 라이선스를 하나 발급함.
	 * @param res
	 */
	private void checkTrialLicense(TypeMap res) {
		Membership membership = res.get("membership");
		User user = membership.getUser();
		if (user == null) {
			return;
		}
		List<License> licenses = res.get("licenses");
		if (licenses.isEmpty() ) {
			Order order = orderService.createTrialOrder(user);
			// 평가판에는 라이선스 1장만 발급함
			licenses.add(order.getItems().get(0));
		}
	}
}
