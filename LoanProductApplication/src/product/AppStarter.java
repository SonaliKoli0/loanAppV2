package product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.*;

import databaseConnector.ArchiveTableSQL;
import databaseConnector.LoanProductSQL;
import databaseHelper.DatabaseHelper;
import utils.Constants;
import utils.DbUtils;
import utils.Util;

public class AppStarter {
	protected static final String GETOLDPRODUCT = "SELECT *FROM ( SELECT lp.* FROM loanproduct lp WHERE lp.status='COMPLETED' OR lp.status='CANCELLED')subquery WHERE EXISTS ( SELECT 1 FROM product p WHERE p.productid = subquery.productid AND p.enddate + INTERVAL '1' YEAR <= CURRENT_DATE)";
	public static void insertArchive() {
		
		PreparedStatement stmt = null;
		int j = 1;
		int id = -1;
		Connection con = null;

		try {
			con = DatabaseHelper.getConnection();
			stmt = con.prepareStatement(GETOLDPRODUCT, Statement.RETURN_GENERATED_KEYS);

			// Retrieve the generated keys
			// Execute the update operation
			
			

			// Execute a separate query to retrieve the last inserted ID
			try (ResultSet generatedKeys = stmt.executeQuery()) {
				if (generatedKeys.next()) {
					id = generatedKeys.getInt("productId");
					
					Product p=LoanProductSQL.readProduct(id);
					p.setProductId((int) id);
					ArchiveTableSQL.insertProductArchive(p);
					ArchiveTableSQL.insertLoanProductArchive(p);
					ArchiveTableSQL.insertDisbursementScheduleArchive(p);
					LoanProductSQL.deleteProduct(id);
					
				} else {
					
				}
			}

		} catch (Exception e) {
			try {
				throw e;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {

			DbUtils.close(stmt, con);
		}
	}
	
	public static HashMap<String, Object> inputs = new HashMap<>();

	public static void addInput(String key, Object val) {
		inputs.put(key, val);
		return;
	}

	public static void main(String[] args) throws Exception {
		try {
			// Establish database connection
			Connection connection = DatabaseHelper.getConnection();
			// Perform database operations here
			System.out.println("APP Started");
			// Close the connection
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		AppStarter.insertArchive();
		AppStarter.getInputs();

	}

	// for performing the action on the product based on the user inputs
	public static void getInputs() throws Exception {
		while (true) {
			try {
				System.out.println(
						"\n\nSelect action you want to perform:\n1.New\n2.Update\n3.Read\n4.Cancel\n5.Cashflow\n6.Report\n7.Exit");
				System.out.println("Select one action from above\n");
				Scanner read = new Scanner(System.in);
				String action = read.next();
				if (action.equals("1") || action.equals("2") || action.equals("3") || action.equals("4")
						|| (action.equals("5") || action.equals("6") || action.equals("7"))) {
					if (action.equals("7")) {

						System.out.println("Thank you :)");
						System.exit(0);

					}

					AppStarter.addInput(Constants.ACTION, action);
					System.out.println("Select product type :\n1.Loan\n");
					System.out.println("Select product Type from above:\n");
					String productType = read.next();
					if (!(productType.equals("1"))) {
						System.out.println("Please select Valid option\n");
						AppStarter.getInputs();
					}
					AppStarter.addInput(Constants.PRODUCTTYPE, productType);

					Product.checkAction(productType, action);
				} else {
					System.out.println("Please select Valid option\n");
					AppStarter.getInputs();
				}
			} catch (Exception e) {
				System.exit(0);
			}
		}
	}
}