package com.example.sensortracker.controller;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import com.example.sensortracker.ActivityWithCallBack;
import com.example.sensortracker.SecondActivity;

public class ConnectSec extends Connect {

	public ConnectSec(ActivityWithCallBack activity, String URL) {
		super(activity, URL);
//		Log.d("url",URL);
		((SecondActivity) activity).callBackAddress(URL.substring(URL.indexOf("coordinator=")+"coordinator=".length()));
	}

	@Override
	public ArrayList<String> getURLandName(String str) {
		ArrayList<String> ans = new ArrayList<String>();
		String regex = "(http://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])\" > ([0-9a-zA-Z:-]+) </A><TD> <a href=xbee-description-([0-9]+).htm>[0-9]+</a> </TD><TD> <a href=http://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]>(((19|20)\\d\\d)/(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01]))</a> </TD><TD>(([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]) </TD>";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			ans.add(matcher.group(1)+">"+matcher.group(2)+">"+matcher.group(3)+">"+matcher.group(4)+">"+matcher.group(9));
		}
		return ans;
	}

	@Override
	ArrayList<String> getAns(ArrayList<String> strList) {
		ArrayList<String> ret = new ArrayList<String>();
		for (int i = 0; i < strList.size(); i++) {
//			Log.d("strlist",strList.get(i));
			ret.add(strList.get(i));
		}
		return ret;
	}
}
