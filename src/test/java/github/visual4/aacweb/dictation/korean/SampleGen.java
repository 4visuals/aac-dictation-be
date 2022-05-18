package github.visual4.aacweb.dictation.korean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class SampleGen {

	public static void main(String[] args) throws SQLException {
		
		String user = "root";
		String pass="1111";
		String path = "jdbc:mariadb://localhost:3306/aacdictdb";
		
		Connection con = DriverManager.getConnection(path, user, pass);
		PreparedStatement stmt = con.prepareStatement("select sentence, level from wr_sentence sen where sen.level >= 1 and type = 'S'");
		ResultSet rs = stmt.executeQuery();
		TreeMap<Integer, List<String>> sens = new TreeMap<>();
		while(rs.next()) {
			String sentence = rs.getString("sentence");
			int level = rs.getInt("level");
			
			List<String> list = sens.getOrDefault(level, new ArrayList<>());
			list.add(sentence);
			sens.putIfAbsent(level, list);
		}
		
		sens.forEach((level, list) -> {
			System.out.println("#L" + level);
			list.forEach(System.out::println);
		});
		
		con.close();
		
				
	}
}
