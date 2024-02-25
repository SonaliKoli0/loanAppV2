package databaseConnector;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import databaseHelper.DatabaseHelper;
import product.LoanProduct;
import product.Product;
import product.Schedule;
import utils.DbUtils;
import utils.Util;

public class ScheduleSQL {

	protected static final String SAVE_DisbursementSchedule = "INSERT INTO  DisbursementSchedule (productID,DisbursementDate,DisbursementAmount) VALUES (?,?,?)";
	/**
	 * Method for adding the disbursement schedule in the DB
	 * @param p
	 * @throws Exception
	 */
	public static void insertDisbursementSchedule(Product p) throws Exception {
		PreparedStatement stmt = null;
		
		int affectedRows = 0;
		LoanProduct lp = (LoanProduct) p;
		Connection con = null;

		try {
			con = DatabaseHelper.getConnection();

			if (p.getStartDate() != null) {
				for (Schedule schedule : p.getDisbursementSchedule()) {
					int index = 1;
					stmt = con.prepareStatement(SAVE_DisbursementSchedule, Statement.RETURN_GENERATED_KEYS);
					stmt.setInt(index++, lp.getProductId());
					stmt.setDate(index++, Util.toSQLDate(schedule.getDate()));
					stmt.setDouble(index++, (schedule.getAmount()));
					affectedRows = stmt.executeUpdate();
				}
			}

			if (affectedRows == 0) {
				throw new SQLException("Inserting product failed, no rows affected.");
			}else{
				 Date currentDate = Util.toSQLDate(Calendar.getInstance().getTime());
				 ActionHistorySQL.insertAction("CREATE", currentDate, p.getProductId());
			}
		} catch (Exception e) {
			throw e;
		} finally {

			DbUtils.close(stmt, con);
		}
	}

	/**
	 * Method for getting the disbursement schedule from the DB
	 * 
	 * @param loanId
	 * @return
	 */
	public static List<Schedule> readDisbursementSchedule(int productId) {

		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection con = null;
		List<Schedule> ls = new ArrayList<Schedule>();
		try {
			con = DatabaseHelper.getConnection();
			stmt = con.prepareStatement("SELECT * FROM disbursementschedule WHERE productId=?");
			stmt.setInt(1, productId);
			rs = stmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("DisbursementId");
				Double amount = rs.getDouble("disbursementamount");
				Date date = rs.getDate("DisbursementDate");
				Schedule sch = new Schedule(id, amount, date);
				ls.add(sch);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			DbUtils.close(stmt, con);

		}
		return ls;

	}
	public static void deleteDisbursementSchedule(int id) {

		PreparedStatement stmt = null;
		Connection con = null;

		try {
			con = DatabaseHelper.getConnection();
			stmt = DbUtils.newPreparedStatement(con, "DELETE FROM disbursementSchedule WHERE PRODUCTID=?");
			stmt.setLong(1, id);

			stmt.executeUpdate();
			System.out.println("Disbursement deleted successfully");

		} catch (Exception e) {
			System.out.println("No disbursement schedule found for loan id " + id);
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