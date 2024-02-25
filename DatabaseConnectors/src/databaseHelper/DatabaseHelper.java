package databaseHelper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHelper {
	private static final String URL = "jdbc:oracle:thin:@//localhost:1521/ADENZA";
	private static final String USERNAME = "C##LOANUSER";
	private static final String PASSWORD = "calypso";

	static {
		try {
			// Load the Oracle JDBC driver class
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new IllegalStateException("Oracle JDBC driver not found", e);
		}
	}
    // for getting the connection with the DB
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USERNAME, PASSWORD);
	}
}