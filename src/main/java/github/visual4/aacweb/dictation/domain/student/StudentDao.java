package github.visual4.aacweb.dictation.domain.student;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.Dao;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.user.User;
import github.visual4.aacweb.dictation.domain.user.User.Column;

@Repository
public class StudentDao {

	final SqlSession session;

	public StudentDao(SqlSession session) {
		super();
		this.session = session;
	}

	public User insertStudent(User student) {
		session.insert(Dao.mapper(this, "insertStudent"), student);
		return student;
	}
	
	public List<User> findStudentsByTeacher(Long teacherSeq) {
		return session.selectList(Dao.mapper(this, "findStudentsByTeacher"), teacherSeq);
	}

	public User findBy(User.Column column, Object value) {
		return session.selectOne(Dao.mapper(this, "findBy"),
				TypeMap.with("colname", column.name(), "value", value));
	}

	public void updateStudent(User student) {
		session.update(Dao.mapper(this, "updateStudent"), student);
	}

	public void updateStudentProp(User student, String prop, Object value) {
		session.update(
			Dao.mapper(this, "updateStudentProp"),
			TypeMap.with(
				"student", student.getSeq(),
				"column", prop,
				"value", value));
		
	}
	/**
	 * 학생 삭제
	 * @param studentSeq
	 * @param teacherSeq
	 */
	public void deleteStudent(Long studentSeq, Long teacherSeq) {
		
		session.delete(
			Dao.mapper(this, "deleteStudent"),
			TypeMap.with(
				"student", studentSeq,
				"teacher", teacherSeq));
	}

	public void changeTeacher(User student) {
		session.update(Dao.mapper(this, "changeTeacher"), student);
		
	}
}
