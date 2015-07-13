package com.example.sensortracker.controller;

import java.util.ArrayList;

import android.util.Log;

import com.example.sensortracker.ActivityWithCallBack;
import com.example.sensortracker.SecondActivity;

public class ConnectSec extends Connect {



	public ConnectSec(ActivityWithCallBack activity, String URL) {
		super(activity, URL);
		Log.d("url",URL);
		((SecondActivity) activity).callBackAddress(URL.substring(URL.indexOf("coordinator=")+"coordinator=".length()));
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
