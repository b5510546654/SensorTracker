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
import java.util.List;
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
	private Date lastUpdate;
	private int checker;
	private String sensorName;
	public Download(ActivityWithCallBack activityWithCallBack,String URL,String time,String value,String period, String address,String type,Date lastUpdate,String sensorName){
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
		this.type = type;
		this.lastUpdate = lastUpdate;
		this.sensorName = sensorName;
		sensor = URL.substring(URL.indexOf("addr="));
//		Log.d("sensor",sensor);
//		Log.d("sensorName",sensorName);
	}

	private void connect(String time){
		Treadconnect treadconnect = new Treadconnect(time, link, sensor, dbHelper,sensorName,this);
		treadconnect.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		checker += 1;
	}

	private void day(Calendar from,Calendar to) {
		String temp = time.split(" ")[0];
		Date timedb = dbHelper.maxTime(temp);
		if(dbHelper.isCompleted(from));
		else if(timedb == null || timedb.getTime() < lastUpdate.getTime()){
			connect(time);
			dbHelper.updateFinishTable(from);
		}
		if(checker == 0)
			finish();
	}

	@Override
	protected String doInBackground(String... s) {
		Calendar[] minmax = minmaxDate();
		checker = 0;
		ret = new ArrayList<String>();
		dbHelper = DBHelper.getInstance(activity,address,type,sensorName);
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
		}
	}

	void finishConnect(){
		checker -= 1;
//		Log.d("counter","finish  "+checker);
		if(checker <= 0){
			finish();
		}
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
		String temp = sdf.format(from.getTime()).split(" ")[0];
		Date timedb = dbHelper.maxTime(temp);
		if(dbHelper.isCompleted(from));
		else if(timedb == null || timedb.getTime() < lastUpdate.getTime()){
			connect(sdf.format(from.getTime()));
			dbHelper.updateFinishTable(from);
		}
		if(checker == 0)
			finish();
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
		if(str.equals("type")) return "type";
		if(str.equals("AD0")) return "ad0";
		if(str.equals("AD1")) return "ad1";;
		if(str.equals("AD2")) return "ad2";
		if(str.equals("AD3")) return "ad3";
		if(str.equals("V")) return "V";
		if(str.equals("TP")) return "TP";
		if(str.equals("RSSI")) return "RSSI";
		return "ad0";

	}

	private void week(Calendar from,Calendar to){
		int lmonth = lastUpdate.getMonth();
		int fmonth = from.get(Calendar.MONTH);
		if(lmonth == fmonth){
			String temp = sdf.format(from.getTime()).split(" ")[0];
			Date timedb = dbHelper.maxTime(temp);
			if(dbHelper.isCompleted(from));
			else if(timedb.getTime() < lastUpdate.getTime()){
				connect(sdf.format(from.getTime()));
				dbHelper.updateFinishTable(from);
			}
		}
		else{
			if(!dbHelper.isCompleted(from)){
				connect(sdf.format(from.getTime()));
				dbHelper.updateFinishTable(from);				
			}
			String temp = sdf.format(to.getTime()).split(" ")[0];
			Date timedb = dbHelper.maxTime(temp);
			if(dbHelper.isCompleted(to));
			else if(timedb.getTime() < lastUpdate.getTime()){
				connect(sdf.format(to.getTime()));
				dbHelper.updateFinishTable(to);
			}
		}
		if(checker == 0)
			finish();
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
	private String sensorName;
	public Treadconnect(String time,String link,String sensor,DBHelper dbHelper,String sensorName,Download download) {
		this.time = time;
		this.link = link;
		this.sensor = sensor;
		this.dbHelper = dbHelper;
		this.sensorName = sensorName;
		this.download = download;
	}

	private void connect(String time){
		String[] ttime = time.split("/");
		List<Sensor> sensorList = new ArrayList<Sensor>();
		try{
			URL oracle = new URL(link+sensor+"&ymd="+ttime[0]+"/"+ttime[1]+"/00");
			URLConnection yc = oracle.openConnection();
			//			Log.d("oracle",oracle.toString());
			BufferedReader in = new BufferedReader(new InputStreamReader(
					yc.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				String[] str = inputLine.split(",");
				Date date = new Date(str[0]+","+str[1]);
				double[] ad = transform(str[6],str[8],str[9],str[10],str[11]);
				Sensor sensor = new Sensor(date,str[3],str[6],ad[0],ad[1],ad[2],ad[3],str[13],str[14],str[15]);
				sensorList.add(sensor);
			}
			dbHelper.addSensorList(sensorList);
			in.close();
		}
		catch(Exception e){
		}
	}

	private double[] transform(String type,String ads0,String ads1,String ads2,String ads3){
		double ad0 = Double.parseDouble(ads0),ad1 = Double.parseDouble(ads1),ad2 = Double.parseDouble(ads2),ad3 = Double.parseDouble(ads3);
		double[] ad = new double[4];
		ad[0] = Math.round((0.097781*ad0-0.14477));
		ad[1] = Math.round((0.1178*ad1-0.00087));
		ad[2] = (((ad2/1023.0) * 1200)-500)/10.0;
		ad[3] = ad3;
		return ad;
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