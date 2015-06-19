package com.example.sensortracker.code;

import java.util.ArrayList;

import com.example.sensortracker.ActivityWithCallBack;

public class ConnectMain extends Connect {



	public ConnectMain(ActivityWithCallBack activity, String URL) {
		super(activity, URL);
	}

	@Override
	ArrayList<String> getAns(ArrayList<String> strList) {
		ArrayList<String> ret = new ArrayList<String>();
		for (int i = 0; i < strList.size(); i+=2) {
			ret.add(strList.get(i));
		}
		return ret;
	}

	


}
