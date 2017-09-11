package cl.magal.asistencia.ui.components;

import java.util.Calendar;

public class MonthSelectorItem {
	public Integer month;
	public Integer year;
	
	MonthSelectorItem(){
		Calendar c =Calendar.getInstance();
		month = c.get(Calendar.MONTH);
		year = c.get(Calendar.YEAR);
	}
	
	public Integer getMonth() {
		return month;
	}
	public void setMonth(Integer month) {
		this.month = month;
	}
	public void addMonth(Integer plus) {
		this.month = month + plus;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public void addYear(Integer plus) {
		this.year = year + plus;
	}
	
}