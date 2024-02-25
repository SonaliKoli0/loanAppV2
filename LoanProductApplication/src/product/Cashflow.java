package product;

import java.util.ArrayList;
import java.util.Date;

import utils.Util;

public class Cashflow {
	
	protected Date date;
	protected int productId;
	protected String type;
	protected String direction;
	
	public Cashflow(int id, Date date, String type, String direction) {
		this.productId=id;
		this.date=date;
		this.type=type;
		this.direction=direction;
	}
	public Cashflow(){
		
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
    
	public String toString(){
		StringBuilder cashflow= new StringBuilder();
		cashflow.append(productId);
		cashflow.append("\t");
		cashflow.append(Util.formatDate(this.getDate()));
		cashflow.append("\t");
		cashflow.append(this.getDirection());
		cashflow.append("\t");
		cashflow.append(getType());
		
		return cashflow+"";
		//return productId+"\t"+Util.formatDate(this.getDate())+"\t"+this.getDirection()+"\t"+getType()+"\t";
	}
	public ArrayList<Cashflow> generateCashflows(Product lp) {
		return null;
	}
	

}
