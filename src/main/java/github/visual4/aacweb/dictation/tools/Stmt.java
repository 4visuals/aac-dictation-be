package github.visual4.aacweb.dictation.tools;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Stmt {

	private PreparedStatement stmt;

	public Stmt(PreparedStatement stmt) {
		this.stmt = stmt;
	}
	
	public void close() {
		Db.close(stmt);
	}

	public Rset select() {
		try {
			return new Rset(stmt.executeQuery());
		} catch (SQLException e) {
			throw new RuntimeException("fail to get result set from stmt", e);
		}
	}

	public int update() {
		try {
			return stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("fail to execute query", e);
		}
		
	}

	public <T> Stmt bind(int i, T val) {
		try {
			Class<?> cls = val.getClass();
			if (cls == Integer.class) {
				stmt.setInt(i, Integer.class.cast(val));
			} else if (cls == String.class) {
				stmt.setString(i, String.class.cast(val));
			} else if (cls.isEnum()) {
				stmt.setString(i, Enum.class.cast(val).name());
			}
			else {
				throw new RuntimeException("unknown type: " + cls.getName());
			}
			return this;
		} catch (SQLException e) {
			throw new RuntimeException("fail to execute query", e);
		}
	}

	public Stmt clear() {
		try {
			stmt.clearParameters();
			return this;
		} catch (SQLException e) {
			throw new RuntimeException("fail to clear params of stmt", e);
		}
	}
	
}
