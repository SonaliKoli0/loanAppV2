package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DbUtils {
	/**
	 *  Method for creating new prepared statement for executing query
	 * @param con
	 * @param st
	 * @return
	 * @throws SQLException
	 */
	 public static PreparedStatement newPreparedStatement(Connection con, String st) throws SQLException {

		if (con == null) {
			throw new SQLException("CalypsoException: ioSQL.newPreparedStatement connection is null");
		}
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(st);
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return stmt;
	}
	/**
	 * Method to close the statement
	 * @param stmt
	 * @param con
	 */
	 static public void close(Statement stmt, Connection con) {
		 if (stmt != null) {
			 try {
				 stmt.close();
			 } catch (Exception e) {
			 }
		 }
		 if (con != null) {
			 try {
				 con.close();
			 } catch (SQLException e) {
				 e.printStackTrace();
			 }
		 }
	 }
}
