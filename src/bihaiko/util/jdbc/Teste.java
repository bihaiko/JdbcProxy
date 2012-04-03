package bihaiko.util.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;

public class Teste {

	public static void main(String[] args) throws Exception {
		
		Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		Connection con = DriverManager.getConnection("jdbc:odbc:Teste");
		con.createStatement().execute("select * from plan1");
	}
}
