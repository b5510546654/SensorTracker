package com.example.sensortracker.code;

public class Sensor {
	private String ymd ;
	private String time;
	private String address;
	private String ip;
	private String type;
	private String ad0 ;
	private String ad1;
	private String ad2;
	private String ad3;
	private String DIO;
	private String V;
	private String TP;
	private String RSSI;
	
	public String getYmd() {
		return ymd;
	}

	public void setYmd(String ymd) {
		this.ymd = ymd;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
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

	public String getAd0() {
		return ad0;
	}

	public void setAd0(String ad0) {
		this.ad0 = ad0;
	}

	public String getAd1() {
		return ad1;
	}

	public void setAd1(String ad1) {
		this.ad1 = ad1;
	}

	public String getAd2() {
		return ad2;
	}

	public void setAd2(String ad2) {
		this.ad2 = ad2;
	}

	public String getAd3() {
		return ad3;
	}

	public void setAd3(String ad3) {
		this.ad3 = ad3;
	}

	public String getDIO() {
		return DIO;
	}

	public void setDIO(String dIO) {
		DIO = dIO;
	}

	public String getV() {
		return V;
	}

	public void setV(String v) {
		V = v;
	}

	public String getTP() {
		return TP;
	}

	public void setTP(String tP) {
		TP = tP;
	}

	public String getRSSI() {
		return RSSI;
	}

	public void setRSSI(String rSSI) {
		RSSI = rSSI;
	}

	public Sensor(String ymd, String time, String address, String ip,
			String type, String ad0, String ad1, String ad2, String ad3,
			String dIO, String v, String tP, String rSSI) {
		super();
		this.ymd = ymd;
		this.time = time;
		this.address = address;
		this.ip = ip;
		this.type = type;
		this.ad0 = ad0;
		this.ad1 = ad1;
		this.ad2 = ad2;
		this.ad3 = ad3;
		DIO = dIO;
		V = v;
		TP = tP;
		RSSI = rSSI;
	}

	@Override
	public String toString() {
		return "Sensor [type=" + type + ", ad0=" + ad0 + ", ad1=" + ad1
				+ ", ad2=" + ad2 + ", ad3=" + ad3 + ", DIO=" + DIO + ", V=" + V
				+ ", TP=" + TP + ", RSSI=" + RSSI + "]";
	}
	
	
}
