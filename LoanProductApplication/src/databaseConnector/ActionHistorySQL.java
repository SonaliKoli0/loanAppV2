package databaseConnector;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import databaseHelper.DatabaseHelper;
import product.LoanProduct;
import utils.DbUtils;

public class ActionHistorySQL {
	protected static final String SAVE_ACTION_DETAIL = "INSERT INTO ActionHistory (product_ID, action, action_date) VALUES (?, ?, ?)";

	public static void insertAction(String action, Date date, int productId) throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = DatabaseHelper.getConnection();
			stmt = con.prepareStatement(SAVE_ACTION_DETAIL);
			stmt.setInt(1, productId);
			stmt.setString(2, action);
			stmt.setDate(3, date);

			int affectedRows = stmt.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException("Inserting action failed, no rows affected.");
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			DbUtils.close(stmt, con);
		}
	}
	public static void deleteAtionHistory(int id) {

		PreparedStatement stmt = null;

		Connection con = null;

		try {

			
			con = DatabaseHelper.getConnection();

			stmt = con.prepareStatement("DELETE FROM actionhistory WHERE PRODUCT_ID=?");
			stmt.setInt(1, id);
			stmt.executeUpdate();
			ProductSQL.deleteProduct(id);
		

		} catch (Exception e) {
			System.out.println("No schedule found for product id " + id);
		} finally {
			
			DbUtils.close(stmt,con);

			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
