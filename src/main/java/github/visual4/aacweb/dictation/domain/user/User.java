package github.visual4.aacweb.dictation.domain.user;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.springframework.util.ReflectionUtils;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

	public enum Column {
		user_seq,
		user_email
	}
	
	@EqualsAndHashCode.Include
	Long seq;
	String email;
	String pass;
	String name;
	Vendor vendor;
	Instant creationTime;
	UserRole role;
	LocalDate birth;
	Long teacherRef;
	List<User> students;
	
	public boolean isTeacher() {
		return role == UserRole.TEACHER;
	}
	public boolean isStudent() {
		return role == UserRole.STUDENT;
	}
	
	public static class Builder {
		User instance;
		
		public Builder(UserRole role) {
			instance = new User();
			instance.setVendor(Vendor.GOOGLE);
			instance.setRole(role);
		}
		public Builder prop(String ... formats) {
			try {
			for (String format : formats) {
				String [] token = format.split(":");
				Method m = ReflectionUtils.getDeclaredMethods(User.class)[0];
				m.invoke(instance, token[1]);
			}
			return this;
			} catch (IllegalAccessException e) {
				throw new AppException(ErrorCode.SERVER_ERROR, 500);
			} catch (IllegalArgumentException e) {
				throw new AppException(ErrorCode.SERVER_ERROR, 500);
			} catch (InvocationTargetException e) {
				throw new AppException(ErrorCode.SERVER_ERROR, 500);
			}
		}
		public Builder birth(String birth) {
			LocalDate ld = LocalDate.parse(birth);
			instance.setBirth(ld);
			return this;
		}
		public Builder teacher(Long teacher) {
			instance.setTeacherRef(teacher);
			return this;
		}
		public User get() {
			return instance;
		}
	}

	public Boolean isStudentOf(User teacher) {
		return this.teacherRef.equals(teacher.getSeq());
	}
	
}
