package com.example.sensortracker.model;

import java.util.Date;

public class ReturnObject implements Comparable<ReturnObject>{
	private Date date;
	private Double value;

	public ReturnObject(Date date, Double value) {
		super();
		this.date = date;
		this.value = value;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	@Override
	public int compareTo(ReturnObject o) {
		return getDate().compareTo(o.getDate());
	}
}
