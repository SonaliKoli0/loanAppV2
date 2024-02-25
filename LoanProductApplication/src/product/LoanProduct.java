package product;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import databaseConnector.LoanProductSQL;
import utils.Constants;
import utils.Util;
import product.Cashflow;
import java.util.ArrayList;
import java.util.Calendar;

public class LoanProduct extends Product {

	protected double rate;
	protected double totalValue;
	protected List<Schedule> disbursementSchedule;
	protected Schedule schedule;

	protected String paymentOption;
	protected String loanType;
	protected String status;
	public LoanProduct() {

	}

	public LoanProduct(int productId, Date startDate, Date endDate, String productType, double totalValue,
			double rate, List<Schedule> disbursementSchedule, ArrayList<Cashflow> cashflows, String paymentOption,String loanType,String status) {

		super(productId, productType, startDate, endDate, cashflows);
		
		this.rate = rate;
		this.totalValue = totalValue;
		this.disbursementSchedule = disbursementSchedule;
		this.paymentOption = paymentOption;
		this.loanType=loanType;
		this.status=status;
		

	}

	public LoanProduct(Product p, double totalValue, double rate, List<Schedule> disbursementSchedule,
			String paymentOption,String loanType,String status) {

		super(p);
		
		this.rate = rate;
		this.totalValue = totalValue;
		this.disbursementSchedule = disbursementSchedule;
		this.paymentOption = paymentOption;
		this.loanType=loanType;
		this.status=status;

	}

	public String getPaymentOption() {
		return paymentOption;
	}

	public void setPaymentOption(String paymentOption) {
		this.paymentOption = paymentOption;
	}
	public String getLoanType() {
		return this.loanType;
	}
	
	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	public double getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(Double totalValue) {
		this.totalValue = totalValue;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public List<Schedule> getDisbursementSchedule() {

		return disbursementSchedule;
	}

	public void setDisbursementSchedule(List<Schedule> disbursementSchedule) {
		this.disbursementSchedule = disbursementSchedule;
	}

	// Method prints disbursement schedule
	public String printDisbursementSchedule() {
		String str = new String();
		str += "{";
		for (Schedule s : disbursementSchedule) {
			str += s.toString() + ",";
		}
		str += "}";
		return str;
	}
	@Override
	public void getCashflow(int productId) {
		Product lp = (LoanProduct) LoanProductSQL.readProduct(productId);

		ArrayList<Cashflow> cashflows = getListOfCashflows(lp);
		lp.setCashflows(cashflows);
		if(cashflows.size()!=0){
			System.out.println("Cashflow for the product with id "+productId);
		System.out.println(LoanCashflow.PID + "\t" + LoanCashflow.FLOW + "\t" + Constants.DATE + "\t\t"
				+ LoanCashflow.TYPE + "\t\t" + Constants.AMOUNT);
		for (int i = 0; i < cashflows.size(); i++) {
			LoanCashflow cashflow = (LoanCashflow) cashflows.get(i);
			System.out.println(cashflow);
		}}
		else{
			System.out.println("Cashflow can not be generated for cancelled loan product");
		}

	}
	public void setStatus(String status){
		
		
		this.status=status;
	}

	@Override
	public ArrayList<Cashflow> getListOfCashflows(Product product) {
		LoanProduct product1 =(LoanProduct) product;
		if(product1.getStatus().equals("CANCELLED")){return null;}
		Cashflow cs = new LoanCashflow();
		return cs.generateCashflows(product);
	}

	/**
	 * updates product details using the product id given by user and the
	 * details user want to update
	 */

	@Override
	public void updateProduct(int productId) {
		try {
			System.out.println(
					"Enter the new end Date in dd/mm/yyyy format for product with Id " + this.getProductId() + ":\n");
			Scanner read = new Scanner(System.in);
			Date newEndDate = Util.parseDate(read.next());
			
			AppStarter.addInput(Constants.ENDDATE, newEndDate);
			if (validateEndDate(newEndDate, productId)) {
				LoanProductSQL.updateProduct(productId);
			} else {
				AppStarter.getInputs();
			}
		} catch (Exception e) {
			System.out.println("Product with product Id " + productId + "does not exist");
		}

	}

	private boolean validateEndDate(Date newEndDate, int productId) {
		LoanProduct lp = new LoanProduct();
		lp = (LoanProduct) lp.readProduct(productId);
		List<Schedule> ds = lp.getDisbursementSchedule();
		if (newEndDate.after(Util.addMonths(ds.get(ds.size() - 1).getDate(), 1))) {
			return true;
		}
		// TODO Auto-generated method stub
		System.out.println("End Date should be at least one month after  last Disbursement Date");
		return false;
	}

	/**
	 * creates the product using the product details given by user
	 */
	@Override
	public void createProduct() {

		try {
			LoanProductSQL.insertLoanProduct(this);
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public List<Schedule> inputDisbursements(int disbursements, Date endDate, Date startDate, double totalValue) {
		double totalDisbursementAmount=0;
		Scanner read = new Scanner(System.in);
		List<Schedule> disbursementSchedule = new ArrayList<Schedule>();
		for (int index = 0; index < disbursements; index++) {
			Schedule sch = new Schedule();
			System.out.println("Enter disbursement date no " + (index+1) +":\n");
			Date date = Util.parseDate(read.next());
			if (index == disbursements - 1) {
				if ((endDate.before(Util.addMonths(date, 1)))) {
					System.out.println("Disbursement date should be before end date and on and after start date!!");
					while ((endDate.before(Util.addMonths(date, 1)))) {
						System.out.println("Enter disbursement date no " + (index+1) +":\n");
						date = Util.parseDate(read.next());
						if ((endDate.before(Util.addMonths(date, 1)))) {
							System.out.println(
									"Disbursement date should be before end date and on and after start date!!");
						}
					}
				}
			}
			if ((date.before(startDate) || date.after(endDate))) {
				System.out.println("Disbursement date should be before end date and on and after start date!!");
				while ((date.before(startDate) || date.after(endDate))) {
					
					System.out.println("Enter disbursement date no :" + (index+1) +"\n");
					date = Util.parseDate(read.next());
					if ((date.before(startDate) && date.after(endDate))) {
						System.out.println("Disbursement date should be before end date and on and after start date!!");
					}
				}
			}
			sch.setDate(date);
			System.out.println("Enter disbursement amount no  " + (index + 1) +" :\n");
			double amount = read.nextDouble();
			if (amount <= 0) {
				System.out.println("Disbursements amount should be greater than 0!!");
				while (amount <= 0) {
					System.out.println("Enter disbursement amount no  " + (index + 1) +":\n");
					amount = read.nextDouble();
					if (amount <= 0) {
						System.out.println("Disbursements amount should be greater than 0!!");
					}
				}
			}
			totalDisbursementAmount += amount;
			sch.setAmount(amount);
			disbursementSchedule.add(sch);

		}
		if(totalDisbursementAmount!=totalValue){
			
				System.out.println("Entered amount is not equal to total value! Please enter it again!");
			
			inputDisbursements(disbursements, endDate, startDate, totalValue);
		}
		return disbursementSchedule;
	}
    @Override
	public void getReport(){
		try {
			LoanProductSQL.getReport();
		} catch (Exception e) {
			// TODO: handle exception
			e.getStackTrace();
		}
	}
	@Override
	public Product buildProduct() throws Exception {
		Scanner read = new Scanner(System.in);
		try {

			System.out.println("Enter start date of Loan in format dd/mm/yyyy: \n");
			Date startDate = Util.parseDate(read.next());
			if (startDate == null) {
				while (startDate == null) {
					System.out.println("Enter start date of Loan in format dd/mm/yyyy: \n");

					startDate = Util.parseDate(read.next());
				}
			}
			System.out.println("Enter end date of Loan in format dd/mm/yyyy: \n");
			Date endDate = Util.parseDate(read.next());
			if (endDate == null) {
				while (endDate == null) {
					System.out.println("Enter end date of Loan in format dd/mm/yyyy: \n");

					endDate = Util.parseDate(read.next());
				}
			}
			if (endDate.before(startDate)) {
				while (endDate.before(startDate)) {
					System.out.println("Enter end date of Loan should be after end date \n");
					System.out.println("Enter end date of Loan in format dd/mm/yyyy: \n");
					endDate = Util.parseDate(read.next());
					if (endDate.before(startDate)) {
						System.out.println("Entered end date of Loan should be after start date \n");
					}
				}
			}
			Date currentDate = Util.toSQLDate(Calendar.getInstance().getTime());
			String newStatus=new String(); 
			if(newStatus.length()==0){
				if(currentDate.before(startDate)){
					newStatus = "PENDING";
					
				}else if(currentDate.after(endDate)){
					newStatus = "COMPLETED";
				
				}else{
					newStatus = "ACTIVE";
					
				}
			}
		
			System.out.println("Enter rate of interest for the Loan: \n");
			double rate = read.nextDouble();
			if (rate <= 0) {
				System.out.println("Rate should be greater than 0!!");
				while (rate <= 0) {
					System.out.println("Enter rate of interest for the Loan: \n");

					rate = read.nextDouble();
					if (rate <= 0) {
						System.out.println("Rate should be greater than 0!!");
					}
				}
			}
			System.out.println("Enter amount for the Loan: \n");
			double totalValue = read.nextDouble();
			if (totalValue <= 0) {
				System.out.println("Loan Value should be greater than 0!!");
				while (totalValue <= 0) {
					System.out.println("Enter amount for the Loan: \n");

					totalValue = read.nextDouble();
					if (totalValue <= 0) {
						System.out.println("Loan Value should be greater than 0!!");
					}
				}
			}
			System.out.println("Enter number of disbursements : \n");
			int disbursements = read.nextInt();
			if (disbursements <= 0) {
				System.out.println("Disbursements number should be greater than 0!!");
				while (disbursements <= 0) {
					System.out.println("Enter number of disbursements : \n");
					disbursements = read.nextInt();
					if (disbursements <= 0) {
						System.out.println("Disbursements number should be greater than 0!!");
					}
				}
			}
			List<Schedule> disbursementSchedule=new ArrayList<>();
			if(disbursements==1){
				Schedule sch = new Schedule();
				sch.setDate(startDate);
				
				sch.setAmount(totalValue);
				disbursementSchedule.add(sch);
				
			}else{
			System.out.println("Enter disbursements details : \n");

			

		
				disbursementSchedule = inputDisbursements(disbursements, endDate, startDate, totalValue);
					
			}
			System.out.println("List of Payment options:\n 1.MONTHLY_PAYMENT\n2.AT_MATURITY\n");
			System.out.println("Select one option from above:\n");
			String paymentOption = read.next();
			switch (paymentOption) {
			case "1":
				paymentOption = "MONTHLY_PAYMENT";
				break;
			case "2":
				paymentOption = "AT_MATURITY";
				break;
			default:
				System.out.println("Select valid option\n");
				break;
			}
			System.out.println("List of Loan Type options:\n 1.Farmer\n2.House\n3.Vehicle\n4.Education\n");
			System.out.println("Select one option from above:\n");
			String loanType = read.next();
			switch (loanType) {
			case "1":
				loanType = "Farmer";
				break;
			case "2":
				loanType = "House";
				break;
			case "3":
				loanType = "Vehicle";
				break;
			case "4":
				loanType = "Education";
				break;
			default:
				System.out.println("Select valid option\n");
				break;
			}
            
			AppStarter.addInput(Constants.STARTDATE, startDate);
			AppStarter.addInput(Constants.SCHEDULE, disbursementSchedule);
			AppStarter.addInput(Constants.ENDDATE, endDate);
			AppStarter.addInput(Constants.RATE, rate);
			AppStarter.addInput(Constants.TOTALVALUE, totalValue);
			AppStarter.addInput(Constants.LOANTYPE, loanType);
            
			LoanProduct loanProduct = new LoanProduct(-1,  startDate, endDate, this.getProductType(), totalValue,
					rate, disbursementSchedule, new ArrayList<Cashflow>(), paymentOption,loanType,newStatus);
			return loanProduct;
		} catch (Exception e) {
			//System.out.print(Constants.DETAILS_ERROR);
			AppStarter.getInputs();
		}
		return null;
	}

	/**
	 * Returns the product details using the productId
	 */
	@Override
	public Product readProduct(int id) {
		Product lp = null;
		try {
			lp = LoanProductSQL.readProduct(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lp;
	}

	/**
	 * Deletes product by using productId
	 */
	@Override
	public void cancelProduct(int ProductId) {

		try {
			LoanProductSQL.cancelProduct(ProductId);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Method for printing the loan product details
	@Override
	public String toString() {
		StringBuilder loanProduct = new StringBuilder();
		loanProduct.append("\nLoan Product Details:\n");
		loanProduct.append(Constants.PRODUCTID);
		loanProduct.append("\t\t");
		loanProduct.append(this.getProductId());
		loanProduct.append("\n\n");
		loanProduct.append(Constants.RATE);
		loanProduct.append("\t\t\t");
		loanProduct.append(rate);
		loanProduct.append("\n\n");
		loanProduct.append(Constants.STARTDATE);
		loanProduct.append("\t\t");
		loanProduct.append(Util.formatDate(getStartDate()));
		loanProduct.append("\n\n");
		loanProduct.append(Constants.ENDDATE);
		loanProduct.append("\t\t\t");
		loanProduct.append(Util.formatDate(getEndDate()));
		loanProduct.append("\n\n");
		loanProduct.append(Constants.TOTALVALUE);
		loanProduct.append("\t\t");
		loanProduct.append(totalValue);
		loanProduct.append("\n\n");
		loanProduct.append(Constants.SCHEDULE);
		loanProduct.append("\t\t");
		loanProduct.append(disbursementSchedule);
		loanProduct.append("\n\n");
		loanProduct.append(Constants.STATUS);
		loanProduct.append("\t\t");
		loanProduct.append(status);

		return loanProduct + "";
	}

	public String getStatus() {
		
		
		 return this.status;
	}
}