package github.visual4.aacweb.dictation.tools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.management.RuntimeErrorException;
import javax.sql.DataSource;

public class Db {

	public static Connection open(DataSource ds) {
		try {
			return ds.getConnection();
		} catch (SQLException e) {
			 throw new RuntimeException("fail to open",e);
		}
	}
	
	public static Stmt stmt(Connection con, String query) {
		try {
			return new Stmt(con.prepareStatement(query));
		} catch (SQLException e) {
			 throw new RuntimeException("fail to open stmt",e);
		}
	}
	
	
	public static Rset rset(PreparedStatement stmt) {
		try {
			return new Rset(stmt.executeQuery());
		} catch (SQLException e) {
			 throw new RuntimeException("fail to get resutlset",e);
		}
	}
	
	public static void close(Connection con) {
		try {
			con.close();
		} catch (SQLException e) {
			throw new RuntimeException("fail to close",e);
		}
	}

	public static int update(PreparedStatement stmt) {
		try {
			return stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("fail to update",e);
		}
	}

	public static void close(PreparedStatement stmt) {
		try {
			stmt.close();
		} catch (SQLException e) {
			throw new RuntimeException("fail to close",e);
		}
		
	}
	
}
