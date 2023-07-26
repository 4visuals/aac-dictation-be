package github.visual4.aacweb.dictation.domain.license;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.Dao;
import github.visual4.aacweb.dictation.ErrorCode;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.license.License.Column;
import github.visual4.aacweb.dictation.domain.user.User;

@Repository
public class LicenseDao {

	final SqlSession session;

	public LicenseDao(SqlSession session) {
		super();
		this.session = session;
	}
	
	public void insertLicense(License license) {
		session.insert(Dao.mapper(this, "insertLicense"), license);
	}

	public License findOneBy(License.Column column, Object value) {
		List<License> licenses = findBy(column, value);
		int size = licenses.size();
		if (size == 0) {
			return null;
		} else if (size > 1) {
			throw new AppException(ErrorCode.APP_BUG, 500,
					String.format("exactly 0 ro 1 license expected, but %d licenses returned.", size));
		} else {
			return licenses.get(0);
		}
	}
	public List<License> findBy(License.Column column, Object value) {
		return session.selectList(
			Dao.mapper(this, "findBy"),
			TypeMap.with("colname", column, "value", value));
	}
	/**
	 *  수강권에 학생을 등록함
	 * @param lcs
	 * @param student
	 */
	public void bindStudent(License lcs, User student) {
		session.update(
			Dao.mapper(this, "bindStudent"), 
			TypeMap.with("license", lcs, "student", student));
		
	}
	/**
	 * 수강권 삭제
	 * @param license
	 */
	public void deleteLicense(License license) {
		session.delete(Dao.mapper(this, "deleteLicense"), license);
	}
	/**
	 * 만료 기간 변경함
	 * @param license
	 */
	public void updateExpirationTime(License license) {
		session.update(Dao.mapper(this, "updateExpirationTime"), license);
		
	}
}
