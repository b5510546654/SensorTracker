package com.example.sensortracker;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class ButtonOnClickListener implements OnClickListener {
	
	private Activity activity;
	private String url;
	private Class<?> nextClass;
	/**
	 * Constructor, use calling activity
	 * @param activity calling activity
	 */
	public ButtonOnClickListener(Activity activity,Class<?> nextClass,String url) {
		this.activity = activity;
		this.nextClass = nextClass;
		this.url = url;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(activity.getApplicationContext(), nextClass);
		intent.putExtra("url", url);
		activity.startActivity(intent);
	}
	
	
}
