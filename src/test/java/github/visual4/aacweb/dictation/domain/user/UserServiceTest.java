package github.visual4.aacweb.dictation.domain.user;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import github.visual4.aacweb.dictation.BaseDao;

@Import(UserDao.class)
class UserServiceTest extends BaseDao{

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
