package product;

import java.text.DecimalFormat;

//import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import databaseConnector.ScheduleSQL;
import utils.Constants;
import utils.Util;

public class LoanCashflow extends Cashflow {
	public static String TOTALPRINCIPALPAID = "Total Principal Paid:";
	public static String TOTALINTERESTPAID = "Total Interest Paid:";
	public static String FLOW = "FLOW";
	public static String PID = "PID";
	public static String TYPE = "TYPE";
	protected double principal;
	protected double totalInterestValue;

	protected static double remainingPrincipal = 0, totalDisbursedAmountInCurrMonth, totalInterestPaid = 0,
			totalPrincipalPaid = 0, paymentAmount, monthlyInterestRate;
	static int disbursementIndex = 0;
	static Date cashflowDate, lastRepayment = null;
	static List<Schedule> ds = null;
	static ArrayList<Cashflow> ls = null;
	int productId = 0;

	public LoanCashflow(int id, Date date, String type, String direction, Double amount) {
		super(id, date, type, direction);
		this.principal = amount;

	}

	public LoanCashflow() {

	}

	public void setPrincipal(double principal) {

	}

	public void setInterestValue(double interesValue) {

	}

	public double getPrincipal() {

		return principal;

	}

	public double getInterestValue() {

		return totalInterestValue;
	}

	

	@Override
	public ArrayList<Cashflow> generateCashflows(Product product) {

		LoanProduct lp = (LoanProduct) product;
		String option = lp.getPaymentOption();
		ds = ScheduleSQL.readDisbursementSchedule(lp.getProductId());
		ls = new ArrayList<Cashflow>();
		productId = product.getProductId();

		Date cashflowStartDate = ds.get(0).getDate();
		long monthsBetween = Util.noOfMonthsBtwGivenDates(cashflowStartDate, product.getEndDate());

		monthlyInterestRate = lp.getRate() / 12.0; // Convert to monthly

		lastRepayment = ds.get(0).getDate();
		DecimalFormat df = new DecimalFormat("#,##0.00");

		// Iterate through each month
		for (int month = 0; month < monthsBetween; month++) {
			cashflowDate = Util.addMonths(ds.get(0).getDate(), month);
			totalDisbursedAmountInCurrMonth = 0;
			totalInterestValue = 0;

			handleDisbursement(product, ls);
			if (option instanceof String && option.equals(Constants.ATMATURITY)) {
				handleRepaymentsAtMaturity(month, monthsBetween);
			} else {
				handleRepayments(month, monthsBetween);
				checkRemainingPrincipal(month, monthsBetween);
			}

		}

		// Output summary
		//System.out.println("\n" + LoanCashflow.TOTALINTERESTPAID + df.format(totalInterestPaid));
		//System.out.println(LoanCashflow.TOTALPRINCIPALPAID + df.format(totalPrincipalPaid) + "\n");
		
		disbursementIndex = 0;
		remainingPrincipal = 0;
		totalInterestPaid=0;
		totalPrincipalPaid=0;
		return ls;
	}

	private void handleRepaymentsAtMaturity(int month, long monthsBetween) {
		double dailyInterestRate = monthlyInterestRate / Util.getNumberOfDaysInMonth(cashflowDate);
		double monthlyInterest = (remainingPrincipal) * dailyInterestRate * Util.getNumberOfDaysInMonth(cashflowDate)
				/ 100;
		remainingPrincipal += totalDisbursedAmountInCurrMonth;
		totalInterestPaid += (monthlyInterest + totalInterestValue);
		totalPrincipalPaid = remainingPrincipal;

		lastRepayment = Util.addMonths(cashflowDate, 1);
		ls.add(new LoanCashflow(productId, Util.addMonths(cashflowDate, 1), Constants.INTEREST, Constants.IN,
				monthlyInterest + totalInterestValue));
		if (monthsBetween - 1 == month) {
			ls.add(new LoanCashflow(productId, Util.addMonths(cashflowDate, 1), Constants.PRINCIPAL,Constants.IN, totalPrincipalPaid));
		}
	}

	// Handle disbursements within the current month
	public void handleDisbursement(Product p, List<Cashflow> ls) {
		totalInterestValue = 0;
		totalDisbursedAmountInCurrMonth = 0;
		while (disbursementIndex < ds.size()
				&& ds.get(disbursementIndex).getDate().before(Util.addMonths(cashflowDate, 1))) {
			double currentDisbursementAmount = ds.get(disbursementIndex).getAmount();
			double dailyInterestRate = monthlyInterestRate
					/ Util.getNumberOfDaysInMonth(ds.get(disbursementIndex).getDate());
			double interest = currentDisbursementAmount * (dailyInterestRate) * Math
					.abs(Util.getDifferenceDays(ds.get(disbursementIndex).getDate(), Util.addMonths(lastRepayment, 1)))
					/ 100;
			LoanCashflow lc2 = new LoanCashflow(p.getProductId(), ds.get(disbursementIndex).getDate(),
					Constants.PRINCIPAL, Constants.OUT, ds.get(disbursementIndex).getAmount());
			
			ls.add(lc2);
			totalDisbursedAmountInCurrMonth += currentDisbursementAmount;
			totalInterestValue += interest;
			disbursementIndex++;
		}
	}

	// Handle repayments within the current month
	public void handleRepayments(int month, long monthsBetween) {
		if (month != monthsBetween) {
			if (remainingPrincipal >= 0) {
				if (remainingPrincipal == 0) {
					remainingPrincipal = totalDisbursedAmountInCurrMonth;
					totalInterestValue = 0;
					totalDisbursedAmountInCurrMonth = 0;
				}

				double monthlyInterest = (remainingPrincipal) * monthlyInterestRate / 100;
				remainingPrincipal += totalDisbursedAmountInCurrMonth;

				paymentAmount = ((remainingPrincipal) / (monthsBetween - month)) + monthlyInterest + totalInterestValue;
				remainingPrincipal -= paymentAmount - (monthlyInterest + totalInterestValue);
				totalInterestPaid += (monthlyInterest + totalInterestValue);
				totalPrincipalPaid += paymentAmount - (monthlyInterest + totalInterestValue);
				lastRepayment = Util.addMonths(cashflowDate, 1);
				ls.add(new LoanCashflow(productId, lastRepayment, Constants.INTEREST, Constants.IN,
						monthlyInterest + totalInterestValue));
				ls.add(new LoanCashflow(productId, lastRepayment, Constants.PRINCIPAL, Constants.IN,
						paymentAmount - (monthlyInterest + totalInterestValue)));
			}
		}
	}

	public void checkRemainingPrincipal(int month, long monthsBetween) {
		if (month == monthsBetween && remainingPrincipal > 0.01) {
			double dailyInterestRate = monthlyInterestRate / Util.getNumberOfDaysInMonth(cashflowDate);
			double interest = totalDisbursedAmountInCurrMonth * (dailyInterestRate)
					* Util.getDifferenceDays(lastRepayment, cashflowDate);
			paymentAmount = remainingPrincipal;
			remainingPrincipal = 0.0;
			LoanCashflow lc1 = new LoanCashflow(productId, cashflowDate, Constants.INTEREST, Constants.IN, interest);
			ls.add(lc1);
			LoanCashflow lc2 = new LoanCashflow(productId, cashflowDate, Constants.PRINCIPAL, Constants.IN,
					paymentAmount);
			ls.add(lc2);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		LoanCashflow other = (LoanCashflow) obj;
		return productId == other.productId && Objects.equals(date, other.date) && Objects.equals(type, other.type)
				&& Objects.equals(direction, other.direction) && Double.compare(principal, other.principal) == 0;
	}

	public String toString() {
		StringBuilder loanCashflow= new StringBuilder();
		loanCashflow.append(this.getProductId());
		loanCashflow.append("\t");
		loanCashflow.append(this.getDirection());
		loanCashflow.append("\t");
		loanCashflow.append(Util.formatDate(this.getDate()));
		loanCashflow.append("\t");
		loanCashflow.append(getType());
		loanCashflow.append("\t");
		loanCashflow.append(this.getPrincipal());
		return loanCashflow+"";
//		return this.getProductId() + "\t" + this.getDirection() + "\t" + Util.formatDate(this.getDate()) + "\t"
//				+ this.getType() + "\t" + this.getPrincipal();
	}

}