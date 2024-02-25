package databaseConnector;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Calendar;
import java.util.List;

import databaseHelper.DatabaseHelper;
import product.AppStarter;
import product.LoanProduct;
import product.Product;
import product.Schedule;
import utils.Constants;
import utils.DbUtils;
import utils.Util;

public class LoanProductSQL extends ProductSQL {

	protected static final String SAVE_LOAN_PRODUCT = "INSERT INTO  LOANPRODUCT (productId,loanvalue,interestrate,payment_type,LoanType,status) VALUES (?,?,?,?,?,?)";
	protected static final String READ_LOAN_PRODUCT = "SELECT * FROM LOANPRODUCT WHERE PRODUCTID=? ";

	/**
	 *  Method for inserting Loan product details in to the database
	 * @param p
	 * @throws Exception
	 */
	public static void insertLoanProduct(Product p) throws Exception {
		PreparedStatement stmt = null;
		int j = 1;
		ProductSQL.insert(p);
		LoanProduct lp = (LoanProduct) p;
		Connection con = null;
		try {
			con = DatabaseHelper.getConnection();
			stmt = con.prepareStatement(SAVE_LOAN_PRODUCT, Statement.RETURN_GENERATED_KEYS);
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
		ScheduleSQL.insertDisbursementSchedule(p);
	}

	/**
	 *  Method for getting the details of loan product from the database
	 * @param id
	 * @return
	 */
	public static Product readProduct(int id) {

		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection con = null;
		Product lp = ProductSQL.readProduct(id);
		try {
			con = DatabaseHelper.getConnection();
			stmt = con.prepareStatement("SELECT * FROM LOANPRODUCT WHERE PRODUCTID=?");
			stmt.setLong(1, id);

			rs = stmt.executeQuery();

			if (rs.next()) {

				int productId = rs.getInt("PRODUCTID");
				double totalValue = rs.getLong("LOANVALUE");
				Double rate = rs.getDouble("INTERESTRATE");
				String option =rs.getString("PAYMENT_TYPE");
				String type=rs.getString("LOANTYPE");
				String status = rs.getString("STATUS");
				Date sDate = (Date) lp.getStartDate();
				Date eDate = (Date) lp.getEndDate();
				 Date currentDate = Util.toSQLDate(Calendar.getInstance().getTime());
				 
						updateStatus(productId,sDate,eDate);
				
				List<Schedule> ls = ScheduleSQL.readDisbursementSchedule(productId);
				lp = new LoanProduct(lp, totalValue, rate, ls,option,type, status);			
//				System.out.println("Displaying Product with id  " +id);

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
		return lp;
	}

	/**
	 *  Method to delete the product from the database
	 * @param id
	 */
	public static void cancelProduct(int id) {
		PreparedStatement stmt = null;
		Connection con = null;
		try {
			con = DatabaseHelper.getConnection();
			stmt = con.prepareStatement("UPDATE loanProduct SET status = ? WHERE productId = ?");
			stmt.setString(1, "CANCELLED");
			stmt.setInt(2, id);
			stmt.executeQuery();
			System.out.println("Product Cancelled successfully");
			 Date currentDate = Util.toSQLDate(Calendar.getInstance().getTime());
			 ActionHistorySQL.insertAction("CANCEL", currentDate, id);
		

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("No Product found for product id " + id);
		} finally {
			DbUtils.close(stmt, con);
		}
	}
	public static void updateStatus(int id,Date startDate,Date endDate) {
		PreparedStatement stmt = null;
		Connection con = null;
		String status;
		try {
			Date currentDate = Util.toSQLDate(Calendar.getInstance().getTime());
			
				if(currentDate.before(startDate)){
					status = "PENDING";
					
				}else if(currentDate.after(endDate)){
					status = "COMPLETED";
				
				}else{
					status = "ACTIVE";
					
				}
			
			con = DatabaseHelper.getConnection();
			stmt = con.prepareStatement("UPDATE loanProduct SET status = ? WHERE productId = ?");
			stmt.setString(1,status);
			stmt.setInt(2, id);
			stmt.executeQuery();
			
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("No Product found for product id " + id);
		} finally {
			DbUtils.close(stmt, con);
		}
	}
	public static void getReport() {
		PreparedStatement stmt = null;
		Connection con = null;

		try {
			con = DatabaseHelper.getConnection();
			Date startDateFilter = Util.toSQLDate((java.util.Date) AppStarter.inputs.get("dateFilter")); // Example
																											// value
			String loanTypeFilter = (String) AppStarter.inputs.get(Constants.LOANTYPE); // Example
																						// value
			// // Prepare SQL query based on user-specified filters
			String query = "SELECT * FROM product p JOIN loanproduct lp ON p.productId = lp.productId WHERE ";
			if (startDateFilter != null && loanTypeFilter != null) {
				query += "p.startDate >= ? AND lp.loanType = ?";
			} else if (startDateFilter != null) {
				query += "p.startDate >= ?";
			} else if (loanTypeFilter != null) {
				query += "lp.loanType = ?";
			}else {
				query = "SELECT * FROM product p JOIN loanproduct lp ON p.productId = lp.productId WHERE 1=1";
			}

			// // Prepare statement
			stmt = con.prepareStatement(query);
			//
			// Set parameters based on user-specified filters
			int parameterIndex = 1;
			if (startDateFilter != null && loanTypeFilter != null) {

				stmt.setDate(parameterIndex++, startDateFilter);
				stmt.setString(parameterIndex, loanTypeFilter);
			} else if (startDateFilter != null) {
				stmt.setDate(parameterIndex++, startDateFilter);
			} else if (loanTypeFilter != null) {
				stmt.setString(parameterIndex, loanTypeFilter);
			} 
			
			// Execute query
			ResultSet resultSet = stmt.executeQuery();
			//
			// // Process result set
			System.out.println("Loan Report\n");
			System.out.println("Product ID\tLoan Type\tStart Date\t End Date\tLoan value\t\tInterest Rate\t\tStatus\n");

			while (resultSet.next()) {
				// Print or process loan product details
				System.out.println();
				System.out.print(resultSet.getInt("productId") +"\t\t"+resultSet.getString("loanType")+"\t\t" + resultSet.getDate("startDate") + "\t "
						+ resultSet.getDate("endDate") + "\t " + resultSet.getDouble("loanValue") + "\t\t"
						+ resultSet.getDouble("interestrate") + "\t\t\t" + resultSet.getString("status"));

			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("");
		} finally {
			DbUtils.close(stmt, con);
		}
		if((boolean)AppStarter.inputs.get("oldProduct")){
	          getOldReport();}
	}
	public static void getOldReport() {
		PreparedStatement stmt = null;
		Connection con = null;

		try {
			con = DatabaseHelper.getConnection();
			Date startDateFilter = Util.toSQLDate((java.util.Date) AppStarter.inputs.get("dateFilter")); // Example
																											// value
			String loanTypeFilter = (String) AppStarter.inputs.get(Constants.LOANTYPE); // Example
																						// value
			// // Prepare SQL query based on user-specified filters
			String query = "SELECT * FROM product_archive p JOIN loanproduct_archive lp ON p.productId = lp.prodId WHERE ";
			if (startDateFilter != null && loanTypeFilter != null) {
				query += "p.startDate >= ? AND lp.loanType = ?";
			} else if (startDateFilter != null) {
				query += "p.startDate >= ?";
			} else if (loanTypeFilter != null) {
				query += "lp.loanType = ?";
			}else {
				query = "SELECT * FROM product_archive p JOIN loanproduct_archive lp ON p.productId = lp.prodId WHERE 1=1";
			}

			// // Prepare statement
			stmt = con.prepareStatement(query);
			//
			// Set parameters based on user-specified filters
			int parameterIndex = 1;
			if (startDateFilter != null && loanTypeFilter != null) {

				stmt.setDate(parameterIndex++, startDateFilter);
				stmt.setString(parameterIndex, loanTypeFilter);
			} else if (startDateFilter != null) {
				stmt.setDate(parameterIndex++, startDateFilter);
			} else if (loanTypeFilter != null) {
				stmt.setString(parameterIndex, loanTypeFilter);
			} 
		
			// Execute query
			ResultSet resultSet = stmt.executeQuery();
			//
			// // Process result set
//			System.out.println("Loan Report\n");
//			System.out.println("Product ID\tLoan Type\tStart Date\t End Date\tLoan value\t\tInterest Rate\t\tStatus\n");

			while (resultSet.next()) {
				// Print or process loan product details
				System.out.println();
				System.out.print(resultSet.getInt("productId") +"\t\t"+resultSet.getString("loanType")+"\t\t" + resultSet.getDate("startDate") + "\t "
						+ resultSet.getDate("endDate") + "\t " + resultSet.getDouble("loanValue") + "\t\t"
						+ resultSet.getDouble("interestrate") + "\t\t\t" + resultSet.getString("status"));

			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("");
		} finally {
			DbUtils.close(stmt, con);
		}
	}
	public static void deleteProduct(int id) {

		PreparedStatement stmt = null;

		Connection con = null;

		try {

			LoanProduct lp = (LoanProduct) LoanProductSQL.readProduct(id);
			ScheduleSQL.deleteDisbursementSchedule(lp.getProductId());
			
			con = DatabaseHelper.getConnection();

			stmt = con.prepareStatement("DELETE FROM LOANPRODUCT WHERE PRODUCTID=?");
			stmt.setInt(1, id);
			stmt.executeUpdate();
			ProductSQL.deleteProduct(id);
			System.out.println("Product deleted successfully");

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
