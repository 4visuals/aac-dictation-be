package github.visual4.aacweb.dictation.domain.user;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.TypeMap;

@Service
public class UserService {

	@Autowired
	GoogleAuthService googleAuthService;
	
	@Autowired
	UserDao userDao;
	
	public Membership getMembership(String vendor, String accessToken) {
		TypeMap profile = googleAuthService.getUserProfile(accessToken);
		String email = profile.getStr("email");
		User user = userDao.findBy("user_email", email);
		
		Membership membership = new Membership(user, profile, Vendor.GOOGLE.name().toLowerCase());
		return membership;
	}

	public Membership login(TypeMap profile) {
		String email = profile.getStr("email");
		User user = userDao.findBy("user_email", email);
		if (user == null) {
			System.out.println(user);
			throw new AppException(ErrorCode.NOT_A_MEMBER, 401);
		}
		return new Membership(user, profile, Vendor.GOOGLE.name().toLowerCase());
	}
	/**
	 * 회원가입
	 * @param profile - map of {email, vendor, }
	 * @return
	 */
	public Membership join(TypeMap profile) {
		String email = profile.getStr("email");
		User user = userDao.findBy("user_email", email);
		if (user != null) {
			throw new AppException(ErrorCode.EXISTING_USER, 409);
		}
		user = new User();
		user.setCreationTime(Instant.now());
		user.setEmail(email);
		user.setVendor(Vendor.GOOGLE);
		user.setPass(UUID.randomUUID().toString());
		userDao.insertUser(user);
		user.setPass(null);
		System.out.println(user.getSeq() + ", " + user.getEmail());
		return new Membership(user, profile, Vendor.GOOGLE.name().toLowerCase());
	}
}
