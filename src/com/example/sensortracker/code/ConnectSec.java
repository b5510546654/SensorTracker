package com.example.sensortracker.code;

import java.util.ArrayList;

import android.app.Activity;
import android.util.Log;

public class ConnectSec extends Connect {



	public ConnectSec(Activity activity, String URL) {
		super(activity, URL);
	}

	@Override
	ArrayList<String> getAns(ArrayList<String> strList) {
		ArrayList<String> ret = new ArrayList<String>();
		for (int i = 0; i < strList.size(); i++) {
			if(!strList.get(i).contains("field")){
				ret.add(strList.get(i));
			}
		}
		return ret;
	}




}
