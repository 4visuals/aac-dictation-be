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
		
		String user = "_____";
		String pass="_____";
		String path = "jdbc:mariadb://localhost:3306/________";
		
		Connection con = DriverManager.getConnection(path, user, pass);
		PreparedStatement stmt = con.prepareStatement("select sentence, level from wr_sentence sen where sen.level >= 1");
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
