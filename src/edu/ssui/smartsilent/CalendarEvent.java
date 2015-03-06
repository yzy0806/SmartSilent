package edu.ssui.smartsilent;

import java.sql.Date;

public class CalendarEvent {
	 
	private String name;
	private String location;
	private Date begin;
	private Date end;
	private boolean allDay;
	private boolean silent;
	/**
	 * This class is to hold information about Calendar Event instance after query information from the calendar contract. 
	 * 
	 * @param name
	 * @param location
	 * @param begin
	 * @param end
	 * @param allDay
	 * 
	 * @author Leo Ying
	 * 
	 *   
	 */
	public CalendarEvent(String name, String location, String begin, String end, String allDay){
		this.name =name;
		this.location=location;
		long start =Long.parseLong(begin);
		this.begin = new Date(start);
		long over = Long.parseLong(end);
		this.end = new Date(over);
		if(allDay.equals("1")){
			this.allDay=true;
		}
		else{
			this.allDay=false;
		}
		this.silent =false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getBegin() {
		return begin;
	}

	public void setBegin(Date begin) {
		this.begin = begin;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public boolean isAllDay() {
		return allDay;
	}

	public void setAllDay(boolean allDay) {
		this.allDay = allDay;
	}

	public boolean isSilent() {
		return silent;
	}

	public void setSilent(boolean silent) {
		this.silent = silent;
	}
	
	public String toString(){
		String result=getName() +isAllDay();
		return result;
	}
	
}
