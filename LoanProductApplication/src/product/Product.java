package product;

import java.util.ArrayList;
import java.util.Date;

import java.util.List;
import java.util.Scanner;

import utils.Constants;
import utils.Util;

public class Product {
	public final static String STARTDATEERROR = "start Date cannot be greater than or equal to endDate!!";
	public final static String TOTALVALUESUMERROR = "Total of disbursed amount should be equals to TotalLoanValue!!";
    public final static String DISBURSEMENTDATERROR="Last disbursement Date should be at least one month before end date !!";
	private int productId;
	private Date startDate;
	private Date endDate;
	private String action;
	private String productType;
	private ArrayList<Cashflow> cashflows;
	public Product() {

	}

	public Product(Product p) {

		this.productId = p.getProductId();
		this.startDate = p.getStartDate();
		this.endDate = p.getEndDate();
		this.productType = p.getProductType();
		this.cashflows=p.getCashflows();
	}

	public Product(int productId, String productType, Date startDate, Date endDate,ArrayList<Cashflow> cashflows) {
		this.productId = productId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.productType = productType;
		this.cashflows=cashflows;
	}

	// returns productId
	public int getProductId() {
		return productId;
	}

	// sets productId
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public ArrayList<Cashflow> getCashflows() {
		return cashflows;
	}

	public void setCashflows(ArrayList<Cashflow> cashflows) {
		this.cashflows = cashflows;
	}
	// returns start date
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public List<Schedule> getDisbursementSchedule() {

		return null;
	}

	/**
	 * method calls the corresponding action that user wants to perform.
	 * 
	 * @param Action
	 * @param p
	 */
	public void callAction(String Action, Product product) {
		switch (Action) {
			case "3": {
				product = product.readProduct(product.getProductId());
				if(product!=null)
				System.out.println(product);
			}
				break;
			case "4": {
				product.cancelProduct(product.getProductId());
			}
				break;
			case "2" : {
				int id = product.getProductId();
				product =  product.readProduct(id);
				product.updateProduct(id);
				product.setProductId(id);
				callAction("3", product);
			}
				break;
			case "5": {
				
				
				product.getCashflow(product.getProductId());
			}
				break;
			case"6":  {
				Scanner read = new Scanner(System.in);
								System.out.println("\n\nSelect below criteria to generate loan report:");
				System.out.println("1. Start Date filter");
				System.out.println("2. Loan Type filter");
				System.out.println("3. Both");
				System.out.println("4. Get all Loans");


				action = read.next();
				Date dateFilter = null;
				String loanType = null;

				switch (action) {
				case "1":
					System.out.println("Enter start date:");
					dateFilter = Util.toSQLDate(read.next());
					break;

				case "2":
					System.out.println("List of Loan Type options:");
					System.out.println("1. Farmer");
					System.out.println("2. House");
					System.out.println("3. Vehicle");
					System.out.println("4. Education");
					System.out.println("Select one option:");
					int option = read.nextInt();
					switch (option) {
					case 1:
						loanType = "Farmer";
						break;
					case 2:
						loanType = "House";
						break;
					case 3:
						loanType = "Vehicle";
						break;
					case 4:
						loanType = "Education";
						break;// default : System.out.println("Select valid
								// option");
					}
					break;

				case "3":
					System.out.println("Enter start date:");
					dateFilter = Util.toSQLDate(read.next());
					System.out.println("List of Loan Type options:");
					System.out.println("1. Farmer");
					System.out.println("2. House");
					System.out.println("3. Vehicle");
					System.out.println("4. Education");
					System.out.println("Select one option:");
					int typeOption = read.nextInt();
					switch (typeOption) {
					case 1:
						loanType = "Farmer";
						break;
					case 2:
						loanType = "House";
						break;
					case 3:
						loanType = "Vehicle";
						break;
					case 4:
						loanType = "Education";
						break;
					// default : System.out.println("Select valid option");
					}
					break;
					
				case "4":
					AppStarter.addInput("allFilter", "allFilter");
					break;

				default:
					System.out.println("Invalid option");
					break;
				}
				System.out.println("You want old product details:\n1.Yes\n2.NO\n");
				String option=read.next();
				
                switch (option) {
				case "1":
					 AppStarter.addInput("oldProduct", true);
					break;
				case "2":
					AppStarter.addInput("oldProduct", false);
					break;

				default:
					break;
				}
				AppStarter.addInput("dateFilter", dateFilter);
				AppStarter.addInput(Constants.LOANTYPE, loanType);
				product.getReport();
				
			}
				break;
			default:
				break;
			}

		
			
		
 
	}
	public void getReport() {
		// TODO Auto-generated method stub

	}

	// gives product details
	protected Product readProduct(int productId) {
		return null;
	}

	// updates product using productId
	protected void updateProduct(int productId) {
	}

	// deletes product using productId
	protected void cancelProduct(int productId) {
	}

	// creates product
	protected void createProduct() {
	}

	// Method for generating cash flow
	protected void getCashflow(int productId) {
		// TODO Auto-generated method stub

	}

	// checks which action wants to perform and calls that action
	public static Product checkAction(String productType,String action) throws Exception {
		Scanner read=new Scanner(System.in);
		try {
			if (productType.equals("1")) {
				productType="Loan";
				LoanProduct loanProduct = new LoanProduct();
				if (action.equals("1")) {
					loanProduct = (LoanProduct) loanProduct.buildProduct();
					loanProduct.setProductType(productType);
					loanProduct.createProduct();
					System.out.println(loanProduct);
					return loanProduct;
 
				} else if( action.equals("6")){
					loanProduct.callAction(action, loanProduct);
				}
					
					else {
					System.out.println("Enter product Id on which you want to perform this "+action+" action:\n");
					int productId=read.nextInt();
					loanProduct.setProductId(productId);
					loanProduct.callAction(action, loanProduct);
					return loanProduct;
				}
			}
			else{
				AppStarter.getInputs();	
			}

		} catch (Exception e) {
			//System.out.print(Constants.DETAILS_ERROR);
			AppStarter.getInputs();
		}
		return null;
	}

	public static void inputValidation(Date startDate, Date endDate, double totalValue, double rate,
			List<Schedule> disbursementSchedule) {
		if(startDate == null || endDate == null){
			System.out.println("startDate or endDate cannot be null");
			System.exit(0);
		}
		
		if (startDate.compareTo(endDate) != -1) {
			System.err.print(Product.STARTDATEERROR);
			System.exit(0);
		}
		if (Util.addMonths(disbursementSchedule.get(disbursementSchedule.size() - 1).getDate(), 1).after(endDate)) {
			System.err.print(Product.DISBURSEMENTDATERROR);
			System.exit(0);
		}


		double checkTotalValue = 0;
		for (int i = 0; i < disbursementSchedule.size(); i++) {
			checkTotalValue += disbursementSchedule.get(i).getAmount();
		}
      
		if (totalValue != checkTotalValue) {
			System.err.print(Product.TOTALVALUESUMERROR);
			System.exit(0);
		}
	}

	// for printing product details
	@Override
	public String toString() {
		StringBuilder product= new StringBuilder();
		product.append(Constants.PRODUCTID);
		product.append("\t\t");
		product.append(this.getProductId());
		product.append("\n");
		product.append(Constants.STARTDATE);
		product.append("\t\t");
		product.append(Util.formatDate(getStartDate()));
		product.append("\n");
		product.append(Constants.ENDDATE);
		product.append("\t\t");
		product.append(Util.formatDate(getEndDate()));
		product.append("\n");
		product.append(Constants.SCHEDULE);
		product.append("\t\t");
		return product+"";

 
	}

	public void createLoanProduct() {
		// TODO Auto-generated method stub

	}

	public Product buildProduct() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<Cashflow> getListOfCashflows(Product product) {
		// TODO Auto-generated method stub
		return null;
	}

}
