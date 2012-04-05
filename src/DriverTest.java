import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jstels.jdbc.csv.CsvDriver2;
import bihaiko.util.jdbc.ProxyDriver;

public class DriverTest {

	public static void main(String[] args) {
		try {
//			Class.forName(CsvDriver2.class.getName());
//			Connection conn = DriverManager.getConnection("jdbc:jstels:csv:.\\csvfiles");
			
			Class.forName(ProxyDriver.class.getName());
			Connection conn = DriverManager.getConnection("jdbc:bihaiko:jstels.jdbc.csv.CsvDriver2@jdbc:jstels:csv:.\\csvfiles");
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM prices");
			print(rs);

			PreparedStatement ps = conn.prepareStatement("SELECT * FROM prices WHERE PRODID=? AND REGIONID=?");
			ps.setInt(1, 100860);
			ps.setInt(2,102);
			rs = ps.executeQuery();
			print(rs);

			rs.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	private static void print(ResultSet rs) throws SQLException {
		
		System.out.println();

		for (int j = 1; j <= rs.getMetaData().getColumnCount(); j++) 
			System.out.print(rs.getMetaData().getColumnName(j) + "\t");
		System.out.println();

		while (rs.next()) {
			for (int j = 1; j <= rs.getMetaData().getColumnCount(); j++) 
				System.out.print(rs.getObject(j) + "\t");
			System.out.println();
		}
	}
}
