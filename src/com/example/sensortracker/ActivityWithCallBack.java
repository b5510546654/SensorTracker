package com.example.sensortracker;

import java.util.ArrayList;

import android.app.Activity;

public abstract class ActivityWithCallBack extends Activity {

	public ActivityWithCallBack() {
		super();
	}

	public abstract void callBack(ArrayList<String> URLs) ;
}