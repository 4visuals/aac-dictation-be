package github.visual4.aacweb.dictation.tools;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.BiConsumer;

public class Rset {

	private ResultSet rs;

	public Rset(ResultSet rs) {
		this.rs = rs;
	}
	
	public void each( BiConsumer<Rset, Integer> cb) {
		int idx = 0;
		try {
			while(rs.next()) {
				cb.accept(this, idx++);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public String str(String colName) {
		try {
			return rs.getString(colName);
		} catch (SQLException e) {
			throw new RuntimeException("error: " + colName, e);
		}
	}
	public Integer Int(String colName) {
		try {
			int v = rs.getInt(colName);
			if (rs.wasNull()) {
				return null;
			} else {
				return v;
			}
		} catch (SQLException e) {
			throw new RuntimeException("error: " + colName, e);
		}
	}
}
