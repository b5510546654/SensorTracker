package com.example.sensortracker.code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.sensortracker.ActivityWithCallBack;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

public class Download extends AsyncTask<String, Integer, String> {

	protected ProgressDialog loading;
	private final String URL;
	private ActivityWithCallBack activity;
	private ArrayList<String> ret;
	DBHelper dbHelper;
	List<ReturnObject> ros;
	private String address;
	private String type;
	private String time;
	private static String sensor;
	private final static String link = "http://ime.ist.hokudai.ac.jp/~yamamoto/xbee/xbee-download-by-day.cgi?";
	private String value;	
	private String period;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


	public Download(ActivityWithCallBack activityWithCallBack,String URL,String time,String value,String period, String address){
		loading = new ProgressDialog(activityWithCallBack);
		loading.setTitle("Sensor");
		loading.setMessage("loading ... ");
		loading.setCancelable(false);
		loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		this.URL = URL;
		this.time = time;
		this.activity = activityWithCallBack;
		this.address = address;
		this.value = value;
		this.period = period;
		sensor = URL.substring(URL.indexOf("addr="));

	}

	public void get_type() throws IOException{
		URL oracle = new URL("http://ime.ist.hokudai.ac.jp/~yamamoto/xbee/xbee-sensor.cgi?"+sensor);
		URLConnection yc = oracle.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				yc.getInputStream()));
		String inputLine;
		String temp = "";
		while ((inputLine = in.readLine()) != null) {
			temp += inputLine;
		}
		in.close();
		type = getType(temp);
	}

	public String getType(String str){
		Pattern pattern = Pattern.compile(", Type: (.*?)<BR>");
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	@Override
	protected String doInBackground(String... s) {
		try {
			get_type();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ret = new ArrayList<String>();
		dbHelper = DBHelper.getInstance(activity,address,type);
		ros = new ArrayList<ReturnObject>();
		selectByPeriod();
		Collections.sort(ros);
		for(int i = 0;i<ros.size();i++){
			ReturnObject ro = ros.get(i);
			ret.add(ro.getDate()+">"+ro.getValue());
		}
		Log.d("RET",ret.size()+"");
		return null;
	}

	private void selectByPeriod(){
		if(period.equals("Date"))
			day();
		else if(period.equals("Week"))
			week();
		else if(period.equals("Month"))
			month();
		else
			day();
	}

	private void day(){
		Calendar to = Calendar.getInstance();
		to.setTime(new Date(time));
		Calendar from = (Calendar)to.clone();
		to.add(Calendar.DATE, 1);
		List<Date> getTime = dbHelper.getTime(sdf.format(from.getTime()), sdf.format(to.getTime()));
		if(!getTime.contains(sdf.format(from.getTime()))){
			connect(time);
		}
		ros = dbHelper.getByDay(valueToString(value),time);
	}

	private void week(){
		Calendar to = Calendar.getInstance();
		to.setTime(new Date(time));
		to.set(Calendar.DAY_OF_WEEK,7);
		Calendar from = (Calendar)to.clone();
		from.set(Calendar.DAY_OF_WEEK, 0);
		if(from.get(Calendar.DATE) >= to.get(Calendar.DATE))
			from.add(Calendar.DATE,-7);
		to.add(Calendar.DATE, 1);
		from.add(Calendar.DATE, 1);
		List<Date> getTime = dbHelper.getTime(sdf.format(from.getTime()),sdf.format(to.getTime()));
		while(from.getTimeInMillis() < to.getTimeInMillis()){
			if(!getTime.contains(sdf.format(from.getTime())))
				connect(sdf.format(from.getTime()));
			from.add(Calendar.DATE,1);
		}
		ros = dbHelper.getByWeek(valueToString(value),time);
	}

	private void month(){
		Calendar to = Calendar.getInstance();
		to.setTime(new Date(time));
		Calendar from = (Calendar)to.clone();
		from.set(Calendar.DATE,1);
		to.add(Calendar.MONTH, 1);
		to.set(Calendar.DATE, 1);
		List<Date> getTime = dbHelper.getTime(sdf.format(from.getTime()),sdf.format(to.getTime()));
		while(from.getTimeInMillis() < to.getTimeInMillis()){
			if(!getTime.contains(sdf.format(from.getTime())))
				connect(sdf.format(from.getTime()));
			from.add(Calendar.DATE,1);
		}
		ros = dbHelper.getByMonth(valueToString(value),time);
	}

	private void connect(String time){
		try{
			URL oracle = new URL(link+sensor+"&ymd="+time);
			Log.d("URL",URL);
			URLConnection yc = oracle.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					yc.getInputStream()));
			String inputLine;
			String temp = "";
			while ((inputLine = in.readLine()) != null) {
				String[] str = inputLine.split(",");
				Date date = new Date(str[0]+","+str[1]);
				Sensor sensor = new Sensor(date,str[3],str[4],str[6],str[8],str[9],str[10],str[11],str[12],str[13],str[14],str[15]);
				dbHelper.addSensor(sensor);
			}
			Log.d("ROS",ros.size()+"");
			in.close();
		}
		catch(Exception e){
		}
	}

	@Override
	protected void onPostExecute(String result) {
		loading.dismiss();
		activity.callBack(ret);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		loading.show();
	}

	public String valueToString(String str){
		if(str.equals("address")) return "address";
		if(str.equals("ip")) return "ip";
		if(str.equals("type")) return "type";
		if(str.equals("AD0")) return "ad0";
		if(str.equals("AD1")) return "ad1";;
		if(str.equals("AD2")) return "ad2";
		if(str.equals("AD3")) return "ad3";
		if(str.equals("DIO")) return "DIO";
		if(str.equals("V")) return "V";
		if(str.equals("TP")) return "TP";
		if(str.equals("RSSI")) return "RSSI";
		return "ad0";

	}
}