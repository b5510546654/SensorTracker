package com.example.sensortracker.code;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
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
	
	public Download(ActivityWithCallBack activity,String URL,String time, String address, String type){
		loading = new ProgressDialog(activity);
		loading.setTitle("Sensor");
		loading.setMessage("loading ... ");
		loading.setCancelable(false);
		loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		this.URL = URL;
		this.time = time;
		this.activity = activity;
		this.address = address;
		this.type = type;
	}

	@Override
	protected String doInBackground(String... s) {
		ret = new ArrayList<String>();
		dbHelper = DBHelper.getInstance(activity);
		sensors = dbHelper.getByDay(address,type,time);
		if(sensors.isEmpty())
			connect();
		for(int i = 0;i<sensors.size();i++){
//			Log.d("sensor","in for loop");
			Sensor sensor = sensors.get(i);
			ret.add(sensor.getAd0());
			if(i+1 == sensors.size())
				Log.d("sensor","in loop : "+i);
		}
		return null;
	}

	private void connect(){
		try{
			URL oracle = new URL(URL);
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
//				Log.d("sensor",sensor.toString());
				//				ret.add(sensor.toString());
				ret.add(str[9]);
			}
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

}