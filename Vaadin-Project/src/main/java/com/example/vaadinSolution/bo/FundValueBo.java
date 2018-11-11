package com.example.vaadinSolution.bo;

/**
 * This class holds value for date and fund price for that particular date
 * @author Shreyas Deshpande
 *
 */
public class FundValueBo {
	
	public FundValueBo() {
	}
	
	public FundValueBo(String date, String value) {
		super();
		this.date = date;
		this.value = value;
	}
	private String date;
	private String value;
	
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

}
