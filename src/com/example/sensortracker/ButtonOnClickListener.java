package com.example.sensortracker;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class ButtonOnClickListener implements OnClickListener {
	
	private Activity activity;
	private String url;
	private String address;
	private String type;
	private Class<?> nextClass;
	private String time;
	/**
	 * Constructor, use calling activity
	 * @param activity calling activity
	 */
	public ButtonOnClickListener(Activity activity,Class<?> nextClass,String url,String time,String address,String type) {
		this.activity = activity;
		this.nextClass = nextClass;
		this.url = url;
		this.time = time;
		this.address = address;
		this.type = type;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(activity.getApplicationContext(), nextClass);
		intent.putExtra("url", url);
		intent.putExtra("time", time);
		intent.putExtra("address", address);
		intent.putExtra("type", type);

		activity.startActivity(intent);
	}
	
	
}
