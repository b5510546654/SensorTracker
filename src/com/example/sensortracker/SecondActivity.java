package com.example.sensortracker;

import java.util.ArrayList;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.sensortracker.controller.Connect;
import com.example.sensortracker.controller.ConnectSec;
public class SecondActivity extends ActivityWithCallBack {
	private String URL ;
	private String address;
	private String text;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		URL = getIntent().getExtras().getString("url");
		text = getIntent().getExtras().getString("text");
//		Log.d("URL","from sec : "+URL);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);
		searchView();
		Connect connect = new ConnectSec(this,URL);
		connect.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_second, menu);
		return true;
	}

	@Override
	public void callBack(ArrayList<String> URLs) {
		LinearLayout scrViewButLay = (LinearLayout)findViewById(R.id.LinearLayoutForButton);		
		myButton = new Button[URLs.size()];
		for(int i = 0;i<URLs.size();i++){
			String[] temp = URLs.get(i).split(">");
			myButton[i] = new Button(this);
			myButton[i].setText(temp[1]);
			scrViewButLay.addView(myButton[i]);
			ButtonOnClickListener boc = new ButtonOnClickListener(this, ThirdActivity.class, temp[0], address, temp[2], temp[3]+" "+temp[4],text+"("+temp[1]+")");
			myButton[i].setOnClickListener(boc);

		}
	}
	
	public void callBackAddress(String address){
		this.address = address;
	}
}
