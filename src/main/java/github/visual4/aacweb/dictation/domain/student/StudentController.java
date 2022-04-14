package github.visual4.aacweb.dictation.domain.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.visual4.aacweb.dictation.Res;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.user.User;
import github.visual4.aacweb.dictation.web.aop.JwtProp;

@RestController
@RequestMapping("/api/student")
public class StudentController {
	
	@Autowired
	StudentService studentService;

	@PostMapping
	public Object registerStudent(@RequestBody TypeMap studentInfo, @JwtProp("useq") Integer teacherSeq) {
		User student = studentService.regiserStudent(teacherSeq.longValue(), studentInfo);
		return Res.success("student", student);
	}
}
