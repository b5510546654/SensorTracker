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
	private String lastUpdate;
	private String text;
	/**
	 * Constructor, use calling activity
	 * @param activity calling activity
	 */
	public ButtonOnClickListener(Activity activity,Class<?> nextClass,String url,String address,String type,String lastUpdate,String text) {
		this.activity = activity;
		this.nextClass = nextClass;
		this.url = url;
		this.address = address;
		this.type = type;
		this.lastUpdate = lastUpdate;
		this.text = text;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(activity.getApplicationContext(), nextClass);
		intent.putExtra("url", url);
		intent.putExtra("address", address);
		intent.putExtra("type", type);
		intent.putExtra("text", text);
		intent.putExtra("lastUpdate", lastUpdate);

		activity.startActivity(intent);
	}
	
	
}
