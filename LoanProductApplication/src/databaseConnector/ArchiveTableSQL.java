package databaseConnector;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;

import databaseHelper.DatabaseHelper;
import product.Cashflow;
import product.LoanProduct;
import product.Product;
import product.Schedule;
import utils.DbUtils;
import utils.Util;

public class ArchiveTableSQL {
	protected static final String SAVE_PRODUCT_ARCHIVE = "INSERT INTO Product_archive (productid,productType, startDate, endDate) VALUES (?,?, ?, ?)";
	protected static final String SAVE_LOAN_PRODUCT_ARCHIVE = "INSERT INTO  LOANPRODUCT_archive (prodId,loanvalue,interestrate,paymenttype,LoanType,status) VALUES (?,?,?,?,?,?)";
	protected static final String SAVE_ACTION_DETAIL = "INSERT INTO Action_History_archive (archive_productID, action, action_date) VALUES (?, ?, ?)";
	protected static final String SAVE_DisbursementSchedule_ARCHIVE = "INSERT INTO  DisbursementSchedule_archive (product_ID,DisbursementDate,DisbursementAmount) VALUES (?,?,?)";

	public static void insertLoanProductArchive(Product p) throws Exception {
		PreparedStatement stmt = null;
		int j = 1;
		
		LoanProduct lp = (LoanProduct) p;
		Connection con = null;
		try {
			con = DatabaseHelper.getConnection();
			stmt = con.prepareStatement(SAVE_LOAN_PRODUCT_ARCHIVE , Statement.RETURN_GENERATED_KEYS);
			if (p.getStartDate() != null) {
				stmt.setInt(j++, p.getProductId());
				stmt.setInt(j++, (int) lp.getTotalValue());
				stmt.setInt(j++, (int) lp.getRate());
				stmt.setString(j++, lp.getPaymentOption());
				stmt.setString(j++, lp.getLoanType());
				stmt.setString(j++, lp.getStatus());
			} else {
				stmt.setNull(j++, Types.DATE);
			}

			int affectedRows = stmt.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException("Inserting product failed, no rows affected.");
			}

		} catch (Exception e) {
			throw e;
		} finally {

			DbUtils.close(stmt, con);
		}
			}
 
	public static void insertDisbursementScheduleArchive(Product p) throws Exception {
		PreparedStatement stmt = null;
		
		int affectedRows = 0;
		LoanProduct lp = (LoanProduct) p;
		Connection con = null;

		try {
			con = DatabaseHelper.getConnection();

			if (p.getStartDate() != null) {
				for (Schedule schedule : p.getDisbursementSchedule()) {
					int index = 1;
					stmt = con.prepareStatement(SAVE_DisbursementSchedule_ARCHIVE, Statement.RETURN_GENERATED_KEYS);
					stmt.setInt(index++, lp.getProductId());
					stmt.setDate(index++, Util.toSQLDate(schedule.getDate()));
					stmt.setDouble(index++, (schedule.getAmount()));
					affectedRows = stmt.executeUpdate();
				}
			}

			if (affectedRows == 0) {
				throw new SQLException("Inserting product failed, no rows affected.");
			}else{
								 
			}
		} catch (Exception e) {
			throw e;
		} finally {

			DbUtils.close(stmt, con);
		}
		insertHistoryArchive(lp);
		System.out.println("Action history deleted successfully");
	}
	public static void insertProductArchive(Product p) throws Exception {
		PreparedStatement stmt = null;
		int j = 1;
		long id = -1;
		Connection con = null;

		try {
			con = DatabaseHelper.getConnection();
			stmt = con.prepareStatement(SAVE_PRODUCT_ARCHIVE, Statement.RETURN_GENERATED_KEYS);
			if (p.getStartDate() != null) {
				stmt.setInt(j++, p.getProductId());
				stmt.setString(j++, p.getProductType());
				stmt.setDate(j++, Util.toSQLDate(p.getStartDate()));
				stmt.setDate(j++, Util.toSQLDate(p.getEndDate()));

			} else {
				stmt.setNull(j++, Types.DATE);
			}

			// Retrieve the generated keys
			// Execute the update operation
			int affectedRows = stmt.executeUpdate();
			System.out.println("Created successfully");

			if (affectedRows == 0) {
				throw new SQLException("Inserting product failed, no rows affected.");
			}

			// Execute a separate query to retrieve the last inserted ID
			

		} catch (Exception e) {
			throw e;
		} finally {

			DbUtils.close(stmt, con);
		}
	}
	public static void insertHistoryArchive(Product p) throws Exception {
		PreparedStatement stmt = null;
		PreparedStatement stmt1 = null;
		int j = 1;
		long id = -1;
		Connection con = null;
		
		try {
			con = DatabaseHelper.getConnection();
			stmt = con.prepareStatement(SAVE_ACTION_DETAIL, Statement.RETURN_GENERATED_KEYS);
			stmt1 = con.prepareStatement("SELECT * FROM Actionhistory WHERE PRODUCT_ID=?");
			stmt1.setLong(1, p.getProductId());

			ResultSet rs = stmt1.executeQuery();

			if (rs.next()) {
				int productId = rs.getInt("PRODUCT_ID");
				Date date = rs.getDate("action_date");
				String action = rs.getString("action");
				
				stmt.setInt(1, productId);
				stmt.setString(2, action);
				stmt.setDate(3, date);


			} else {
				System.out.println("There is no product with id " + id);
				
			}
			
				
			
			
			// Retrieve the generated keys
			// Execute the update operation
			int affectedRows = stmt.executeUpdate();
			System.out.println("Created successfully");
			
			if (affectedRows == 0) {
				throw new SQLException("Inserting product failed, no rows affected.");
			}
			
			// Execute a separate query to retrieve the last inserted ID
			
			
		} catch (Exception e) {
			throw e;
		} finally {
			
			DbUtils.close(stmt, con);
		}
	}
	
}
