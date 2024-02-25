package product;

import java.util.Date;

import utils.Constants;
import utils.Util;

public class Schedule {
	private int id;
	private Date date;
	private Double amount;

	public Schedule(int id, Double amount, java.sql.Date date) {

		this.id = id;
		this.amount = amount;
		this.date = Util.toUtilDate(date);

	}

	public Schedule() {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String toString() {
		StringBuilder schedule= new StringBuilder();
		schedule.append("\n");
		schedule.append(Constants.ID);
		schedule.append("\t");
		schedule.append(getId());
		schedule.append("\t");
		schedule.append(Constants.DATE);
		schedule.append("\t");
		schedule.append(Util.formatDate(getDate()));
		schedule.append("\t");
		schedule.append(Constants.AMOUNT);
		schedule.append("\t");
		schedule.append(getAmount());
		return schedule+"";
		//return Constants.ID+"=" + getId() + ","+ Constants.DATE+"=" + Util.formatDate(getDate()) + ","+ Constants.AMOUNT+"=" + getAmount();
	}
}
