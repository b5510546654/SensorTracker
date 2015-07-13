package com.example.sensortracker.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.example.sensortracker.ActivityWithCallBack;
import com.example.sensortracker.model.ReturnObject;
import com.example.sensortracker.model.Sensor;

public class Download extends AsyncTask<String, Integer, String> {

	private static String sensor;
	private final static String link = "http://ime.ist.hokudai.ac.jp/~yamamoto/xbee/xbee-download-by-day.cgi?";
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	protected ProgressDialog loading;
	private ActivityWithCallBack activity;
	private ArrayList<String> ret;
	DBHelper dbHelper;
	List<ReturnObject> ros;
	private String address;
	private String type;
	private String time;	
	private String value;
	private String period;
	private Map<String,String> map;
	private int checker;
	public Download(ActivityWithCallBack activityWithCallBack,String URL,String time,String value,String period, String address){
		loading = new ProgressDialog(activityWithCallBack);
		loading.setTitle("Sensor");
		loading.setMessage("loading ... ");
		loading.setCancelable(false);
		loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		this.time = time;
		this.activity = activityWithCallBack;
		this.address = address;
		this.value = value;
		this.period = period;
		sensor = URL.substring(URL.indexOf("addr="));
	}

	private void connect(String time){
		Treadconnect treadconnect = new Treadconnect(time, link, sensor, dbHelper,this);
		treadconnect.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		checker += 1;
//		Log.d("counter","connect "+checker);
	}

	private void day(Calendar from,Calendar to) {
		Calendar now = Calendar.getInstance();
		List<Date> getTime = dbHelper.getTime(sdf.format(from.getTime()), sdf.format(to.getTime()));
		//		for (Date date : getTime) {
		//			Log.d("date",""+sdf.format(date.getTime()));
		//		}
		if(!getTime.contains(from.getTime())){
			connect(time);
		}
		else{
			String temp = time.split(" ")[0];
			Date lasttime = new Date(temp+" "+map.get(temp));
			Date timedb = dbHelper.maxTime(temp);
			if(timedb.getTime() < lasttime.getTime()){
				dbHelper.deleteTime(temp);
				connect(time);
			}
		}
		if(checker == 0)
			finish();
		//		ros = dbHelper.getByDay(valueToString(value),time);
	}

	@Override
	protected String doInBackground(String... s) {
		Calendar[] minmax = minmaxDate();
		map = new HashMap<String, String>();
		checker = 0;
		try {
			getTypeDate(minmax[0],minmax[1]);

		} catch (IOException e) {
			e.printStackTrace();
		}
		ret = new ArrayList<String>();
		dbHelper = DBHelper.getInstance(activity,address,type);
		ros = new ArrayList<ReturnObject>();
		selectByPeriod(minmax[0],minmax[1]);
		try {
			if(checker > 0)
			synchronized( this ) {
				wait();
			}
		} catch (InterruptedException e) {
			return null;
		}			
		return null;
	}

	private void finish() {
		if(period.equals("Date"))
			ros = dbHelper.getByDay(valueToString(value),time);
		else if(period.equals("Week"))
			ros = dbHelper.getByWeek(valueToString(value),time);
		else if(period.equals("Month"))
			ros = dbHelper.getByMonth(valueToString(value),time);
		else
			ros = dbHelper.getByDay(valueToString(value),time);
		Collections.sort(ros);
		for(int i = 0;i<ros.size();i++){
			ReturnObject ro = ros.get(i);
			ret.add(ro.getDate()+">"+ro.getValue());
		}
		synchronized( this ){
			this.notifyAll();
//			Log.d("notify","notify");
		}
	}

	void finishConnect(){
		checker -= 1;
		Log.d("counter","finish  "+checker);
		if(checker <= 0){
			finish();
		}
	}

	public void getLastDate(String str){//,String time){
//		String [] temp = time.split(" ");
		String reg = "<TD> ([1-9][0-9][0-9][0-9]/[0-9][0-9]/[0-9][0-9]) </TD>    <TD> (([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]) </TD>";
//		String reg = "    <TD> (([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]) </TD><TD ALIGN=RIGHT><A href=\"http://ime.ist.hokudai.ac.jp/~yamamoto/xbee/xbee-sensordata.cgi\\?"+sensor+"&ymd="+temp[0];
		//		String reg = "<TR><TD> "+temp[0]+" </TD>\n    <TD> (.*?) </TD>";
		//		Log.d("time",temp[0]);
		//		Log.d("reg",reg);
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			//			Log.d("matcher",matcher.group(1));
//			map.put(temp[0],matcher.group(1));
			map.put(matcher.group(1), matcher.group(2));
//			return;
		}
		return ;
	}

	public String getType(String str){
		Pattern pattern = Pattern.compile(", Type: (.*?)<BR>");
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	public void getTypeDate(Calendar from,Calendar to) throws IOException{
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
		
//		type = getType(temp);
//		Calendar run = (Calendar)from.clone();
//		while(run.getTimeInMillis() < to.getTimeInMillis()){
			getLastDate(temp);//,sdf.format(run.getTime()));
//			run.add(Calendar.DATE,1);
//		}
	}

	private Calendar[] minmaxDate(){
		Calendar to = Calendar.getInstance();
		to.setTime(new Date(time));
		Calendar from = null;
		if(period.equals("Date")){
			from = (Calendar)to.clone();
			to.add(Calendar.DATE, 1);
		}
		else if(period.equals("Week")){
			to.set(Calendar.DAY_OF_WEEK,7);
			from = (Calendar)to.clone();
			from.set(Calendar.DAY_OF_WEEK, 0);
			if(from.get(Calendar.DATE) >= to.get(Calendar.DATE))
				from.add(Calendar.DATE,-7);
			to.add(Calendar.DATE, 1);
			from.add(Calendar.DATE, 1);
		}
		else if(period.equals("Month")){
			from = (Calendar)to.clone();
			from.set(Calendar.DATE,1);
			to.add(Calendar.MONTH, 1);
			to.set(Calendar.DATE, 1);		
		}
		else{
			from = (Calendar)to.clone();
			to.add(Calendar.DATE, 1);
		}
		Calendar[] retc = {from,to};
		return retc;
	}

	private void month(Calendar from,Calendar to) {
		Calendar now = Calendar.getInstance();
		List<Date> getTime = dbHelper.getTime(sdf.format(from.getTime()),sdf.format(to.getTime()));
		while(from.getTimeInMillis() < to.getTimeInMillis()){
			if(!getTime.contains(from.getTime()) && from.getTimeInMillis() < now.getTimeInMillis())
				connect(sdf.format(from.getTime()));
			else if(from.getTimeInMillis() < now.getTimeInMillis()){
				String temp = sdf.format(from.getTime()).split(" ")[0];
				Date lasttime = new Date(temp+" "+map.get(temp));
				Date timedb = dbHelper.maxTime(temp);
				if(timedb.getTime() < lasttime.getTime()){
					dbHelper.deleteTime(temp);
					connect(sdf.format(from.getTime()));
				}
			}
			from.add(Calendar.DATE,1);
		}
		if(checker == 0)
			finish();

		//		ros = dbHelper.getByMonth(valueToString(value),time);
	}


	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		loading.show();
	}

	private void selectByPeriod(Calendar from,Calendar to){
		if(period.equals("Date"))
			day(from,to);
		else if(period.equals("Week"))
			week(from,to);
		else if(period.equals("Month"))
			month(from,to);
		else
			day(from,to);
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

	private void week(Calendar from,Calendar to) {
		Calendar now = Calendar.getInstance();
		List<Date> getTime = dbHelper.getTime(sdf.format(from.getTime()),sdf.format(to.getTime()));
		//		for (Date date : getTime) {
		//			Log.d("date",""+sdf.format(date.getTime()));
		//		}
		while(from.getTimeInMillis() < to.getTimeInMillis()){
			//			Log.d("from",sdf.format(from.getTime()));
			if(!getTime.contains(from.getTime()) && from.getTimeInMillis() < now.getTimeInMillis()){
				connect(sdf.format(from.getTime()));
			}
			else if(from.getTimeInMillis() < now.getTimeInMillis()){
				String temp = sdf.format(from.getTime()).split(" ")[0];
				//				Log.d("temp",sdf.format(from.getTime()).split(" ")[0]);
				Date lasttime = new Date(temp+" "+map.get(temp));
				Date timedb = dbHelper.maxTime(temp);
				if(timedb.getTime() < lasttime.getTime()){
					dbHelper.deleteTime(temp);
					connect(sdf.format(from.getTime()));
				}
			}
			from.add(Calendar.DATE,1);
		}
		if(checker == 0)
			finish();

		//		ros = dbHelper.getByWeek(valueToString(value),time);
	}
	@Override
	protected void onPostExecute(String result) {
		loading.dismiss();
		activity.callBack(ret);
	}
}

class Treadconnect extends AsyncTask<String, Integer, String>{
	private String time;
	private String link;
	private String sensor;
	private DBHelper dbHelper;
	private Download download;
	public Treadconnect(String time,String link,String sensor,DBHelper dbHelper,Download download) {
		this.time = time;
		this.link = link;
		this.sensor = sensor;
		this.dbHelper = dbHelper;
		this.download = download;
	}

	private void connect(String time){
		try{
			URL oracle = new URL(link+sensor+"&ymd="+time);
			URLConnection yc = oracle.openConnection();
//			Log.d("oracle",oracle.toString());
			BufferedReader in = new BufferedReader(new InputStreamReader(
					yc.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				String[] str = inputLine.split(",");
				Date date = new Date(str[0]+","+str[1]);
				Sensor sensor = new Sensor(date,str[3],str[4],str[6],str[8],str[9],str[10],str[11],str[12],str[13],str[14],str[15]);
				//				Log.d("sensor",sensor.toString());
				dbHelper.addSensor(sensor);
			}
			//						Log.d("ROS",ros.size()+"");
//			Log.d("time",count2.getTimeInMillis()-count.getTimeInMillis()+"");
			in.close();
		}
		catch(Exception e){
		}
	}

	@Override
	protected String doInBackground(String... params) {
		connect(time);
		download.finishConnect();
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
	}

}