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
import github.visual4.aacweb.dictation.domain.user.User.Column;
import github.visual4.aacweb.dictation.service.rule.Rules;

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
	

	public User loadAdmin(Long adminSeq) {
		User user = this.findUser(adminSeq);
		if (!configService.isAdmin(user)) {
			throw new AppException(ErrorCode.NOT_A_ADMIN, 403);
		}
		return user;
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
	/**
	 * id_token을 통한 로그인
	 * @param vendor
	 * @param idToken
	 * @return
	 */
	public TypeMap getMembershipFromIdToken(String vendor, String idToken) {
		TypeMap param = TypeMap.with("token", idToken);
		TypeMap profile = googleAuthService.getUserProfile(param);
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
		return buildLoginResponse(profile, user, Vendor.GOOGLE);
	}

	/**
	 * 카드 결제 심사용 계정을 위해서 추가함 - id, password를 입력받아서 테스트 계정을 조회함
	 * @param id
	 * @param pass
	 * @return 
	 */
	public TypeMap loginManually(TypeMap payload) {
		String id = payload.getStr("id");
		String pass = payload.getStr("password");
		User user = userDao.loginManually(id, pass);
		if (user == null) {
			throw new AppException(ErrorCode.NOT_A_MEMBER, 401);
		}
		TypeMap profile = TypeMap.with("email", user.getEmail(), "name", user.getName());
		return buildLoginResponse(profile, user, Vendor.MANUAL);
	}

	private TypeMap buildLoginResponse(TypeMap profile, User user, Vendor vendor) {
		user.setStudents(studentDao.findStudentsByTeacher(user.getSeq()));
		profile.put("useq", user.getSeq());
		profile.put("aac_id", user.getEmail());
		/*
		 * 만료 여부와 관계없이 발급됐던 모든 라이선스를 전부 가져옴
		 * - 로그인했을때 라이선스가 하나도 없으면 무료 라이선스를 하나 발급함.
		 */
		List<License> licenses = licenseService.findsBy(License.Column.receiver_ref, user.getSeq());
		Membership membership = new Membership(user, profile, vendor.name().toLowerCase());
		return TypeMap.with("membership", membership,  "licenses", licenses);
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
		user.setUserId(email);
		user.setEmail(email);
		user.setVendor(Vendor.GOOGLE);
		user.setPass(UUID.randomUUID().toString());
		user.setRole(UserRole.TEACHER);
		user.setStudents(Collections.emptyList());
		// user.setBirth(LocalDate.of(1900, 1, 1));
		userDao.insertUser(user);
		profile.put("useq", user.getSeq());
		
		/*
		AppConfiguration config = configService.getConfiguration();
		Product product = productService.findBy(Product.Column.prod_seq, 1);
		Util.notNull(product, ErrorCode.SERVER_ERROR, 500, "no such product seq(" + 1 + ")");
		int cnt = config.getFreeCertsPerUser();
		List<License> licenses = licenseService.createLicenses(product, cnt, null, (lcs) -> {
			lcs.setIssuerRef(adminAccount.getSeq());
			lcs.setReceiverRef(user.getSeq());
			lcs.setDurationInHours(License.UNLIMITED);
		});
		*/
		List<License> licenses = Collections.emptyList();
		
		user.setPass(null);
//		System.out.println(user.getSeq() + ", " + user.getEmail());
		Membership membership = new Membership(user, profile, Vendor.GOOGLE.name().toLowerCase());
	
		return TypeMap.with(
				"membership", membership, 
				"licenses", licenses,
				"students", Collections.<User>emptyList());
	}
	/**
	 * 회원 가입 - 직접 입력
	 * PG사 심사를 위해서 추가함(toss)
	 * 
	 * @param joinForm
	 * @return
	 */
	public User joinManually(JoinDto joinForm) {
		isValidateProperty("email", joinForm.getEmail());
		isValidateProperty("pass", joinForm.getPassword());
		
		Instant currentTime = Instant.now();
		User user = new User();
		user.setName(joinForm.getUserId());
		user.setUserId(joinForm.getUserId());
		user.setEmail(joinForm.getEmail());
		user.setPass(joinForm.getPassword());
		user.setCreationTime(currentTime);
		user.setVendor(Vendor.MANUAL);
		user.setRole(UserRole.TEACHER);
		user.setStudents(Collections.emptyList());
		
		userDao.insertUser(user);
		
		user.setPass(null);
		System.out.println(user.getSeq() + ", " + user.getEmail());
		// Membership membership = new Membership(user, profile, Vendor.GOOGLE.name().toLowerCase());
	
		
		return user;
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
	/**
	 * 주어진 선생님의 학생 조회
	 * @param teacherSeq
	 * @return
	 */
	public List<User> findStudents(Long teacherSeq) {
		User teacher = userDao.findBy(User.Column.user_seq, teacherSeq);
		if(!teacher.isTeacher()) {
			throw new AppException(ErrorCode.NOT_A_TEACHER, 400);
		}
		return studentDao.findStudentsByTeacher(teacherSeq);
	}
	public User findUser(Long userSeq) {
		return studentDao.findBy(User.Column.user_seq, userSeq);
	}
	
	public User findTeacher(String teacherId) {
		return userDao.findBy(User.Column.user_id, teacherId);
	}
	/**
	 * 사용자 검색
	 * @param keyword
	 * @return
	 */
	public List<User> searchUsers(String keyword) {
		return userDao.searchUsers(keyword, 
				TypeMap.with(User.Column.user_role.name(), UserRole.TEACHER.getCode()));
	}
	/**
	 * 존재하는지 확인함 
	 * @param col
	 * @param value
	 * @return
	 */
	public boolean exist(User.Column col, Object value) {
		User any = userDao.findBy(col, value);
		return any != null;
	}

	public Object isValidateProperty(String column, Object value) {
		if ("userId".equals(column)) {
			// 중복 아이디 확인
			String userId = value.toString();
			User existingUser = userDao.findBy(Column.user_id, userId);
			if (existingUser != null) {
				throw new AppException(ErrorCode.DUP_USER_ID, 409);
			}
			Rules.checkUserId(userId);
			return userId;
		} else if ("email".equals(column)) {
			String email = value == null ? null : value.toString().trim();
			if (email == null || email.length() == 0) {
				throw new AppException(ErrorCode.NULL_USER_EMAIL, 422);
			}
			User existingUser = userDao.findBy(User.Column.user_email, email);
			if (existingUser != null) {
				throw new AppException(ErrorCode.DUP_USER_EMAIL, 409);
			}
			return Rules.checkUserEmail(value.toString());
		}  else if ("name".equals(column)) {
			if (value == null || value.toString().trim().length() == 0) {
				throw new AppException(ErrorCode.NULL_USER_NAME, 422);
			}
			return Rules.checkUserName(value.toString());
		} else if ("pass".equals(column)) {
			String pass = value.toString();
			Rules.checkPassword(pass);
			return pass;
		} else if ("birth".equals(column)) {
			System.out.println(value);
			LocalDate time = LocalDate.parse(value.toString());
			return time;
		}
		else {
			throw new AppException(ErrorCode.INVALID_VALUE, 422);
		}
		
	}

	public String findPassword(Long teacherSeq) {
		return userDao.findPassword(teacherSeq);
	}

	public boolean updatePassword(Long teacherSeq, String newPass, String curPass) {
		isValidateProperty("pass", newPass);
		return userDao.updatePassword(teacherSeq, newPass, curPass);
		
	}

	public void deleteUser(User teacher) {
		userDao.deleteUser(teacher.getSeq());
	}
	
}
