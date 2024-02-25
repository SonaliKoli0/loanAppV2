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
import java.util.List;

import databaseHelper.DatabaseHelper;
import product.AppStarter;
import product.Cashflow;
import product.LoanProduct;
import product.Product;
import product.Schedule;
import utils.DbUtils;
import utils.Util;

public class ProductSQL {
	protected static final String SAVE_PRODUCT = "INSERT INTO Product (productType, startDate, endDate) VALUES (?, ?, ?)";

	/**
	 *  Method to insert the product in to the database
	 * @param p
	 * @throws Exception
	 */
	public static void insert(Product p) throws Exception {
		PreparedStatement stmt = null;
		int j = 1;
		long id = -1;
		Connection con = null;

		try {
			con = DatabaseHelper.getConnection();
			stmt = con.prepareStatement(SAVE_PRODUCT, Statement.RETURN_GENERATED_KEYS);
			if (p.getStartDate() != null) {
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
			try (ResultSet generatedKeys = stmt
					.executeQuery("SELECT productId FROM Product WHERE rownum = 1 ORDER BY productId DESC")) {
				if (generatedKeys.next()) {
					id = generatedKeys.getLong("productId");
					p.setProductId((int) id);
				} else {
					throw new SQLException("Inserting product failed, no ID obtained.");
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {

			DbUtils.close(stmt, con);
		}
	}
	
	/**
	 *  Method to get the product details from the database based on the productId
	 * @param id
	 * @return
	 */
	public static Product readProduct(int id) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection con = null;
		Product p = null;
		try {
			con = DatabaseHelper.getConnection();
			stmt = con.prepareStatement("SELECT * FROM PRODUCT WHERE PRODUCTID=?");
			stmt.setLong(1, id);

			rs = stmt.executeQuery();

			if (rs.next()) {
				int productId = rs.getInt("PRODUCTID");
				Date startDate = rs.getDate("STARTDATE");
				String productType = rs.getString("PRODUCTTYPE");
				Date endDate = rs.getDate("ENDDATE");
				p = new Product(productId, productType, startDate, endDate,new ArrayList<Cashflow>());

			} else {
				System.out.println("There is no product with id " + id);
				return null;
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
		return p;
	}

	/**
	 *  Method to delete the product details from the database based on the productId
	 * @param id
	 */
	public static void updateProduct(int id ) {
		Date newEndDate = Util.toSQLDate((java.util.Date)AppStarter.inputs.get("endDate"));
		LoanProduct lp = new LoanProduct();
		lp = (LoanProduct) lp.readProduct(id);
		if(lp.getStatus().equals("CANCELLED") || lp.getStatus().equals("COMPLETED") ){
			System.out.println(lp.getStatus()+" loan can not be updated");
			try {
				AppStarter.getInputs();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		List<Schedule> ds = lp.getDisbursementSchedule();
		Date lastDisburesmentDate = Util.toSQLDate(ds.get(ds.size()-1).getDate());
		if(lastDisburesmentDate.compareTo(newEndDate) != -1){
			System.err.println("End Date should be after last Disbursement Date");
			try {
				AppStarter.getInputs();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		int index = 1;
		PreparedStatement stmt = null;
		Connection con = null;
		try {
			String sql = "UPDATE product SET";
			if (AppStarter.inputs.containsKey("endDate")) {
				sql += " endDate =?";
			}
			sql += " WHERE PRODUCTID = ?";

			con = DatabaseHelper.getConnection();
			stmt = con.prepareStatement(sql);

			 if (AppStarter.inputs.containsKey("endDate")) {
				stmt.setDate(index++, Util.toSQLDate((java.util.Date)AppStarter.inputs.get("endDate")));
			}
			stmt.setInt(index++, id);
			stmt.executeUpdate();
			System.out.println("product updated successfully");
			LoanProductSQL.updateStatus(id,Util.toSQLDate(lp.getStartDate()),newEndDate);
			 Date currentDate = Util.toSQLDate(Calendar.getInstance().getTime());
			 ActionHistorySQL.insertAction("UPDATE", currentDate, id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtils.close(stmt, con);
		}
	}
	public static void deleteProduct(int id) {
		PreparedStatement stmt = null;

		Connection con = null;

		try {
			con = DatabaseHelper.getConnection();
			stmt = con.prepareStatement("DELETE  FROM PRODUCT WHERE PRODUCTID=?");
			stmt.setLong(1, id);
      ActionHistorySQL.deleteAtionHistory(id);
			stmt.executeUpdate();

		} catch (Exception e) {
			System.err.println(e.getMessage());
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