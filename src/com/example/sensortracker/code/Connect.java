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

public abstract class Connect extends AsyncTask<String, Integer, ArrayList<String>> {

	protected ProgressDialog loading;
	private final String URL;

	public static ArrayList<String> getURLandName(String str) {
		ArrayList<String> ans = new ArrayList<String>();
		Pattern pattern = Pattern.compile("<TD><A href=(.*?)</A>");
	    Matcher matcher = pattern.matcher(str);
	    while (matcher.find()) {
	        ans.add(matcher.group(1));
	    }
		return ans;
	}
	
	public Connect(Activity activity,String URL){
		loading = new ProgressDialog(activity);
		loading.setTitle("Sensor");
		loading.setMessage("loading ... ");
		loading.setCancelable(false);
		loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		this.URL = URL;
	}

	@Override
	protected ArrayList<String> doInBackground(String... s) {
		try{
		Log.d("title", URL);
		URL oracle = new URL(URL);
		URLConnection yc = oracle.openConnection();
		Log.d("title","can connect");
		BufferedReader in = new BufferedReader(new InputStreamReader(
				yc.getInputStream()));
		String inputLine;
		String temp = "";
		while ((inputLine = in.readLine()) != null) {
			temp += inputLine;
		}
		in.close();
		return getAns(getURLandName(temp));	
		}
		catch(Exception e){
			return null;
		}
		
	}

	ArrayList<String> getAns(ArrayList<String> strList) {
		return null;
	}

	@Override
	protected void onPostExecute(ArrayList<String> result) {
		loading.dismiss();
	}

	@Override
	protected void onPreExecute() {
		loading.show();
		super.onPreExecute();
	}

}