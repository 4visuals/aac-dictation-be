package github.visual4.aacweb.dictation.domain.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.visual4.aacweb.dictation.Res;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.service.TokenService;
import github.visual4.aacweb.dictation.web.aop.JwtProp;

@RestController
@RequestMapping("/api/user")
public class UserControlller {

	@Autowired
	UserService userService;
	
	@Autowired
	TokenService tokenService;
	
	@PostMapping("/membership")
	public Object checkMembership(@RequestBody TypeMap params) {
		String vendor = params.getStr("vendor");
		String type = params.getStr("type");
		String accessToken = params.getStr("token"); // access token from google oauth
		Membership membership = userService.getMembership(vendor, accessToken);
		
		String jwtToken = tokenService.generateJwt(membership.getProfile());
		return Res.success("membership", membership, "jwt", jwtToken);
	}
	
	@PostMapping("/login")
	public Object login(@JwtProp TypeMap payload) {
		
		Membership membership = userService.login(payload);
		String jwtToken = tokenService.generateJwt(membership.getProfile());
		return Res.success("membership", membership, "jwt", jwtToken);
	}
	
	@PostMapping("/join")
	public Object join(@JwtProp TypeMap profile) {
		Membership membership = userService.join(profile);
		String jwtToken = tokenService.generateJwt(membership.getProfile());
		return Res.success("membership", membership, "jwt", jwtToken);
	}
}
