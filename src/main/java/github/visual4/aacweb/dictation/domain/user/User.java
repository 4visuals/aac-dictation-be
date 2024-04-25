package github.visual4.aacweb.dictation.domain.user;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.springframework.util.ReflectionUtils;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.domain.license.License;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class User {

	public enum Column {
		user_seq,
		user_email,
		user_role, 
		user_id
	}
	
	@EqualsAndHashCode.Include
	Long seq;
	String email;
	String userId;
	String pass;
	String name;
	Vendor vendor;
	Instant creationTime;
	UserRole role;
	LocalDate birth;
	Long teacherRef;
	User teacher;
	List<User> students;
	Boolean admin;
	
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
	public Integer getAgeInMonth(Instant now) {
		// FIXME 가라로 넣음
		return 36;
	}
	public Boolean isLicenseOwner(License license) {
		return license.getReceiverRef().equals(this.seq);
	}
	/**
	 * userId가 같은지
	 * @param form
	 * @return
	 */
	public boolean isSameUserId(User other) {
		return other != null && this.userId != null && this.userId.equals(other.userId);
	}
	
}
