package com.example.sensortracker.code;

import java.util.Date;

public class Sensor implements Comparable<Sensor>{
	private int id;
	private Date datetime;
	private String address;
	private String ip;
	private String type;
	private double ad0 ;
	private double ad1;
	private double ad2;
	private double ad3;
	private double DIO;
	private double V;
	private double TP;
	private double RSSI;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getDatetime() {
		return datetime;
	}
	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public double getAd0() {
		return ad0;
	}
	public void setAd0(double ad0) {
		this.ad0 = ad0;
	}
	public double getAd1() {
		return ad1;
	}
	public void setAd1(double ad1) {
		this.ad1 = ad1;
	}
	public double getAd2() {
		return ad2;
	}
	public void setAd2(double ad2) {
		this.ad2 = ad2;
	}
	public double getAd3() {
		return ad3;
	}
	public void setAd3(double ad3) {
		this.ad3 = ad3;
	}
	public double getDIO() {
		return DIO;
	}
	public void setDIO(double dIO) {
		DIO = dIO;
	}
	public double getV() {
		return V;
	}
	public void setV(double v) {
		V = v;
	}
	public double getTP() {
		return TP;
	}
	public void setTP(double tP) {
		TP = tP;
	}
	public double getRSSI() {
		return RSSI;
	}
	public void setRSSI(double rSSI) {
		RSSI = rSSI;
	}

//	public String getByString(String str){
//		if(str.equals("address")) return address;
//		if(str.equals("ip")) return ip;
//		if(str.equals("type")) return type;
//		if(str.equals("AD0")) return ad0+"";
//		if(str.equals("AD1")) return ad1+"";
//		if(str.equals("AD2")) return ad2+"";
//		if(str.equals("AD3")) return ad3+"";
//		if(str.equals("DIO")) return DIO+"";
//		if(str.equals("V")) return V+"";
//		if(str.equals("TP")) return TP+"";
//		if(str.equals("RSSI")) return RSSI+"";
//		return ad0+"";
//	}
	@Override
	public String toString() {
		return "Sensor [id=" + id + ", datetime=" + datetime + ", address="
				+ address + ", ip=" + ip + ", type=" + type + ", ad0=" + ad0
				+ ", ad1=" + ad1 + ", ad2=" + ad2 + ", ad3=" + ad3 + ", DIO="
				+ DIO + ", V=" + V + ", TP=" + TP + ", RSSI=" + RSSI + "]";
	}
	public Sensor(Date datetime, String address, String ip, String type,
			double ad0, double ad1, double ad2, double ad3, double DIO,
			double V, double TP, double RSSI) {
		super();
		this.datetime = datetime;
		this.address = address;
		this.ip = ip;
		this.type = type;
		this.ad0 = ad0;
		this.ad1 = ad1;
		this.ad2 = ad2;
		this.ad3 = ad3;
		this.DIO = DIO;
		this.V = V;
		this.TP = TP;
		this.RSSI = RSSI;
	}
	
	public Sensor(Date datetime, String address, String ip, String type,
			String ad0, String ad1, String ad2, String ad3, String DIO,
			String V, String TP, String RSSI) {
		super();
		this.datetime = datetime;
		this.address = address;
		this.ip = ip;
		this.type = type;
		this.ad0 = Double.parseDouble(ad0);
		this.ad1 = Double.parseDouble(ad1);
		this.ad2 = Double.parseDouble(ad2);
		this.ad3 = Double.parseDouble(ad3);
		this.DIO = Double.parseDouble(DIO);
		this.V = Double.parseDouble(V);
		this.TP = Double.parseDouble(TP);
		this.RSSI = Double.parseDouble(RSSI);
	}
	
	@Override
	public int compareTo(Sensor o) {
	    return getDatetime().compareTo(o.getDatetime());
	}
	
}
