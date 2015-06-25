package com.example.sensortracker;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class SearchOnClickListener implements OnClickListener {

	private EditText text;
	private ActivityWithCallBack activityWithCallBack;
	public SearchOnClickListener(EditText text, ActivityWithCallBack activityWithCallBack) {
		this.text = text;
		this.activityWithCallBack = activityWithCallBack;

	}

	@Override
	public void onClick(View v) {
		activityWithCallBack.filter(text.getText().toString());
	}


}
