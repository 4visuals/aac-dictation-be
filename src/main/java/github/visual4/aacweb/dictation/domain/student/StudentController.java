package github.visual4.aacweb.dictation.domain.student;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
import github.visual4.aacweb.dictation.domain.user.Membership;
import github.visual4.aacweb.dictation.domain.user.User;
import github.visual4.aacweb.dictation.domain.user.UserRole;
import github.visual4.aacweb.dictation.domain.user.UserService;
import github.visual4.aacweb.dictation.service.TokenService;
import github.visual4.aacweb.dictation.web.aop.JwtProp;

@RestController
@RequestMapping("/api/student")
public class StudentController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	StudentService studentService;

	@Autowired
	LicenseService licenseService;
	
	@Autowired
	TokenService tokenService;
	
	@Autowired
	ExamService examService;
	
	@Autowired
	RecentPaperService recentPaperService;
	
	@PostMapping
	public Object registerStudent(@RequestBody TypeMap studentInfo, @JwtProp("useq") Integer teacherSeq) {
		User student = studentService.regiserStudent(teacherSeq.longValue(), studentInfo);
		
		String lcsUUID = studentInfo.getStr("license");
		TypeMap lcs = null;
		if (lcsUUID != null) {
			lcs = licenseService.bindStudent(
					teacherSeq.longValue(),
					lcsUUID,
					student.getSeq(),
					userService);
		}
		return Res.success("student", student, "lcs", lcs);
	}
	/**
	 * 학생이 로그인 화면에서 id와 password를 입력해서 로그인 시도 
	 * @param payload - ({id, password})
	 * @return
	 */
	@PostMapping("/login")
	public Object login(@RequestBody TypeMap payload) {
		String id = payload.getStr("id");
		String pass = payload.getStr("password");
		TypeMap res  = studentService.login(id, pass, false);
		String jwtToken = tokenService.generateJwt(
				res.<Membership>get("membership").getProfile(),
				UserRole.STUDENT);
		res.put("jwt", jwtToken);
		
		/*
		 * 학생인 경우 segment별 최근 시험 이력을 같이 보냄
		 * 성취도 표시에 사용됨
		 */
		List<License> licenses = res.get("licenses");
		License lcs = licenses.get(0);
		TypeMap exams = examService.queryBySectionChunk(lcs.getUuid());
		res.put("segments", exams.get("quiz"));
		
		User student = res.get("student");
		List<RecentPaper> records = recentPaperService.findWrongAnswersByStudent(student.getTeacherRef(), student.getSeq());
		res.put("records", records);
		
		res.remove("student");
		
		return Res.success(res);
	}
	
//	@PutMapping("/{studentSeq}")
//	public Object updateStudent(@JwtProp("useq") Integer teacherSeq, @RequestBody User studentForm) {	
//		User student = studentService.update(teacherSeq.longValue(), studentForm);
//		return Res.success("student", student);
//	}
	
	@PutMapping("/{studentSeq}")
	public Object updateStudent(@JwtProp("useq") Integer teacherSeq, @RequestBody TypeMap param) {
		Long studentSeq = param.asLong("studentSeq");
		String prop = param.getStr("prop");
		Object value = param.get("value");
		User student = studentService.updateStudent(teacherSeq.longValue(), studentSeq, prop, value);
		return Res.success("student",student);
	}
	
	@DeleteMapping("/{studentSeq}")
	public Object deleteStudent(@JwtProp("useq") Integer teacherSeq, @PathVariable Integer studentSeq) {
		
		User student = studentService.deleteStudent(teacherSeq.longValue(), studentSeq.longValue());
		return Res.success("student", student);
	}
}
