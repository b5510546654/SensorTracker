package com.example.sensortracker.code;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

public class Download extends AsyncTask<String, Integer, String> {

	protected ProgressDialog loading;
	private final String URL;
	
	public Download(Activity activity,String URL){
		loading = new ProgressDialog(activity);
		loading.setTitle("Sensor");
		loading.setMessage("loading ... ");
		loading.setCancelable(false);
		loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		this.URL = URL;
	}

	@Override
	protected String doInBackground(String... s) {
		try{
		URL oracle = new URL(URL);
		URLConnection yc = oracle.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				yc.getInputStream()));
		String inputLine;
		String temp = "";
		while ((inputLine = in.readLine()) != null) {
			temp += inputLine+"\n";
		}
		in.close();
		return temp;
		}
		catch(Exception e){
			return null;
		}
		
	}

	@Override
	protected void onPostExecute(String result) {
		loading.dismiss();
	}

	@Override
	protected void onPreExecute() {
		loading.show();
		super.onPreExecute();
	}

}