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
	List<Sensor> sensors;
	private String address;
	private String type;
	private String time;
	private static String sensor;
	private final static String link = "http://ime.ist.hokudai.ac.jp/~yamamoto/xbee/xbee-download-by-day.cgi?";
	private String value;	
	private String period;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");


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
		dbHelper = DBHelper.getInstance(activity);
		sensors = new ArrayList<Sensor>();
		selectByPeriod();
		for(int i = 0;i<sensors.size();i++){
			Sensor sensor = sensors.get(i);
			ret.add(sensor.getDatetime()+">"+sensor.getByString(value));
			if(i+1 == sensors.size())
				Log.d("sensor","in loop : "+i);
		}
		return null;
	}

	private void selectByPeriod(){
		Log.d("selectByValue",value);
		if(period.equals("Date"))
			day();
		else if(period.equals("Week"))
			week();
		else if(period.equals("Month"))
			month();
	}

	private void day(){
		Log.d("DAY","in day");
		sensors = dbHelper.getByDay(address,type,time);
		if(sensors.size() <= 0){
			Log.d("sensor","empty");
			connect(time);
		}
	}

	private void week(){
		sensors = dbHelper.getByWeek(address, type, time);
		Calendar to = Calendar.getInstance();
		to.setTime(new Date(time));
		to.set(Calendar.DAY_OF_WEEK,7);
		Calendar from = (Calendar)to.clone();
		from.set(Calendar.DAY_OF_WEEK, 0);
		if(from.get(Calendar.DATE) >= to.get(Calendar.DATE))
			from.add(Calendar.DATE,-7);
		to.add(Calendar.DATE, 1);
		from.add(Calendar.DATE, 1);
		List<Date> getTime = dbHelper.getTime(address,type,sdf.format(from.getTime()),sdf.format(to.getTime()));
		while(from.getTimeInMillis() < to.getTimeInMillis()){
			if(!getTime.contains(sdf.format(from.getTime())))
				connect(sdf.format(from.getTime()));
			from.add(Calendar.DATE,1);
		}
	}

	private void month(){
		sensors = dbHelper.getByMonth(address, type, time);
		Calendar to = Calendar.getInstance();
		to.setTime(new Date(time));
		Calendar from = (Calendar)to.clone();
		from.set(Calendar.DATE,1);
		to.add(Calendar.MONTH, 1);
		to.set(Calendar.DATE, 1);
		List<Date> getTime = dbHelper.getTime(address,type,sdf.format(from.getTime()),sdf.format(to.getTime()));
		while(from.getTimeInMillis() < to.getTimeInMillis()){
			if(!getTime.contains(sdf.format(from.getTime())))
				connect(sdf.format(from.getTime()));
			from.add(Calendar.DATE,1);
		}
	}

	private void connect(String time){
		try{
			URL oracle = new URL(link+sensor+"&ymd="+time);
			Log.d("oracle",oracle.toString());
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
				sensors.add(sensor);
			}
			in.close();
		}
		catch(Exception e){
		}
	}

	@Override
	protected void onPostExecute(String result) {
		loading.dismiss();
		Log.d("POST EXE",ret.get(ret.size()-1));
		activity.callBack(ret);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		loading.show();
	}

}