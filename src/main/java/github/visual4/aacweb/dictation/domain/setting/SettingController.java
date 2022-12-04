package github.visual4.aacweb.dictation.domain.setting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.visual4.aacweb.dictation.Res;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.web.aop.JwtProp;

@RestController
@RequestMapping("/api/setting")
public class SettingController {

	@Autowired
	SettingService setting;
	
	// Password
	@PutMapping("/password")
	public Object changePassword(
			@JwtProp("useq") Integer teacherSeq, 
			@RequestBody TypeMap param) {
		String newPass = param.getStr("newPass");
		String curPass = param.getStr("curPass");
		setting.changePassword(teacherSeq.longValue(), newPass, curPass);
		return Res.success("password", newPass);
	}
	/**
	 * 비밀번호 변경 전 입력 화면
	 * @param teacherSeq
	 * @param param
	 * @return
	 */
	@PostMapping("/password/unlock")
	public Object unlockPassword(
			@JwtProp("useq") Integer teacherSeq, 
			@RequestBody TypeMap param) {
		String password = param.getStr("password");
		setting.unlockPassword(teacherSeq.longValue(), password);
		return Res.success();
	}
	/**
	 * 회원 탈퇴
	 * @return
	 */
	@DeleteMapping("/unsubscribe")
	public Object unsubscribe(@JwtProp("useq") Integer teacherSeq) {
		setting.unsubscribe(teacherSeq.longValue());
		return Res.success(true);
	}
}
