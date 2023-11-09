package github.visual4.aacweb.dictation.domain.user;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import github.visual4.aacweb.dictation.BaseDao;
import github.visual4.aacweb.dictation.domain.appconfig.ImportAppConfig;
import github.visual4.aacweb.dictation.domain.license.LicenseServiceTest;
import github.visual4.aacweb.dictation.domain.student.StudentDao;

@Import(UserDao.class)
public class UserServiceTest extends BaseDao{

	@Import({UserService.class,
		StudentDao.class, 
		UserDao.class,
		ImportAppConfig.class,
		GoogleAuthService.class,
		LicenseServiceTest.Imports.class
	})
	public static class Imports {}
	
	public static class Sample {
		public static User teacher1() {
			User user = new User();
			user.setSeq(682L);
			user.setName("마우스");
			user.setEmail("nole.coding@gmail.com");
			user.setRole(UserRole.TEACHER);
			return user;
		}

		public static User admin() {
			User admin = teacher1();
			admin.setSeq(1L);
			admin.setName("SuperAdmin");
			admin.setEmail("admin@aacdict");
			admin.setRole(UserRole.TEACHER);
			return admin;
		}
	}
	
	@Autowired
	UserDao userDao;
	
	@Test
	void testJoin() {
		User user = new User();
		user.setEmail("dummy@naver.com");
		user.setName("dummy");
		user.setPass("dummy");
		user.setCreationTime(Instant.now());
		user.setRole(UserRole.TEACHER);
		user.setVendor(Vendor.GOOGLE);
		
		userDao.insertUser(user);
		
		assertNotNull(user.getSeq());
		
		User actual = userDao.findBy(User.Column.user_seq, user.getSeq());
		assertNotNull(actual);
		assertEquals(UserRole.TEACHER, actual.getRole());
	}

}
