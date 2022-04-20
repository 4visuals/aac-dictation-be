package github.visual4.aacweb.dictation.domain.user;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.Util;
import github.visual4.aacweb.dictation.domain.appconfig.AppConfigService;
import github.visual4.aacweb.dictation.domain.appconfig.AppConfiguration;
import github.visual4.aacweb.dictation.domain.license.LicenseService;
import github.visual4.aacweb.dictation.domain.license.License;
import github.visual4.aacweb.dictation.domain.product.Product;
import github.visual4.aacweb.dictation.domain.product.ProductService;
import github.visual4.aacweb.dictation.domain.student.StudentDao;

@Service
@Transactional
public class UserService {

	final GoogleAuthService googleAuthService;
	final AppConfigService configService;
	final ProductService productService;
	final LicenseService licenseService;
	final UserDao userDao;
	final StudentDao studentDao;
	
	private User adminAccount;
	private Integer adminSeq;
	
	public UserService(
			GoogleAuthService googleAuthService,
			AppConfigService configService, 
			ProductService productService,
			LicenseService licenseService,
			UserDao userDao,
			StudentDao studentDao,
			@Value("${dictation.admin.seq}") Integer adminSeq) {
		this.googleAuthService = googleAuthService;
		this.configService = configService;
		this.productService = productService;
		this.licenseService = licenseService;
		this.userDao = userDao;
		this.studentDao = studentDao;
		this.adminSeq = adminSeq;
	}
	
	@PostConstruct
	public void loadAdminAccount() {
		adminAccount = userDao.findBy(User.Column.user_seq, adminSeq);
	}

	public TypeMap getMembership(String vendor, String accessToken) {
		TypeMap profile = googleAuthService.getUserProfile(accessToken);
		String email = profile.getStr("email");
		User user = userDao.findBy(User.Column.user_email, email);
		if (user == null) {
			Membership membership = new Membership(user, profile, Vendor.GOOGLE.name().toLowerCase());
			return TypeMap.with("membership", membership,  "licenses", Collections.EMPTY_LIST);			
		} else {
			return login(profile);
		}
	}

	public TypeMap login(TypeMap profile) {
		String email = profile.getStr("email");
		User user = userDao.findBy(User.Column.user_email, email);
		if (user == null) {
			throw new AppException(ErrorCode.NOT_A_MEMBER, 401);
		}
		user.setStudents(studentDao.findStudentsByTeacher(user.getSeq()));
		profile.put("useq", user.getSeq());
		// 만료되지 않은 라이선스만 반환
		Instant now = Instant.now();
		List<License> licenses = licenseService.findsBy(License.Column.receiver_ref, user.getSeq());
		List<License> activeLicenses = licenses
				.stream()
				.filter(lcs -> lcs.isAlive(now))
				.collect(Collectors.toList());
		Membership membership = new Membership(user, profile, Vendor.GOOGLE.name().toLowerCase());
		return TypeMap.with("membership", membership,  "licenses", activeLicenses);
	}
	/**
	 * 회원가입
	 * @param profile - map of {email, vendor, }
	 * @return map of {membership: Membership, licenses: [License, ..]}
	 */
	public TypeMap join(TypeMap profile) {
		Instant currentTime = Instant.now();
		String email = profile.getStr("email");
		User existing = userDao.findBy(User.Column.user_email, email);
		if (existing != null) {
			throw new AppException(ErrorCode.EXISTING_USER, 409);
		}
		User user = new User();
		user.setCreationTime(currentTime);
		user.setName(profile.getStr("name"));
		user.setEmail(email);
		user.setVendor(Vendor.GOOGLE);
		user.setPass(UUID.randomUUID().toString());
		user.setRole(UserRole.TEACHER);
		user.setStudents(Collections.emptyList());
		// user.setBirth(LocalDate.of(1900, 1, 1));
		userDao.insertUser(user);
		profile.put("useq", user.getSeq());
		
		AppConfiguration config = configService.getConfiguration();
		Product product = productService.findBy(Product.Column.prod_seq, 1);
		Util.notNull(product, ErrorCode.SERVER_ERROR, 500, "no such product seq(" + 1 + ")");
		
		int cnt = config.getFreeCertsPerUser();
		List<License> licenses = licenseService.createLicenses(product, cnt, null, (lcs) -> {
			lcs.setIssuerRef(adminAccount.getSeq());
			lcs.setReceiverRef(user.getSeq());
		});
		
		user.setPass(null);
		System.out.println(user.getSeq() + ", " + user.getEmail());
		Membership membership = new Membership(user, profile, Vendor.GOOGLE.name().toLowerCase());
	
		return TypeMap.with(
				"membership", membership, 
				"licenses", licenses,
				"students", Collections.<User>emptyList());
	}
	/**
	 * 교사 조회(교사가 아니면 예외 던짐)
	 * @param userSeq
	 * @return
	 */
	public User findTeacher(Long userSeq) {
		User teacher = userDao.findBy(User.Column.user_seq, userSeq);
		if (!teacher.isTeacher()) {
			throw new AppException(ErrorCode.NOT_A_TEACHER, 403);
		}
		return teacher;
	}
	/**
	 * 주어진 선생님의 학생 조회
	 * @param studentSeq 학생 PK
	 * @param teacherSeq 선생님 PK
	 * @return
	 */
	public User findStudent(Long studentSeq, Predicate<User> fn) {
//		studentDao.findStudentsByTeacher(studentSeq);
		User student = studentDao.findBy(User.Column.user_seq, studentSeq);
		if (fn.test(student)) {
			return student;
		} else {
			return null;
		}
	}
	public User findUser(Long userSeq) {
		return studentDao.findBy(User.Column.user_seq, userSeq);
	}
}
