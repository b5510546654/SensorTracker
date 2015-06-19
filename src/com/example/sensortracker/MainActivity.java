package com.example.sensortracker;

import java.util.ArrayList;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.sensortracker.code.*;
public class MainActivity extends ActivityWithCallBack {
	private final String URL = "http://ime.ist.hokudai.ac.jp/~yamamoto/xbee/xbee-coordinators.cgi";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ConnectMain connect = new ConnectMain(this,URL);
		connect.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void callBack(ArrayList<String> URLs) {
		LinearLayout scrViewButLay = (LinearLayout)findViewById(R.id.LinearLayoutForButton);
		Button[] myButton = new Button[URLs.size()];
		for(int i = 0;i<URLs.size();i++){
			String[] temp = URLs.get(i).split(">");
			myButton[i] = new Button(this);
			myButton[i].setText(temp[1]);
			scrViewButLay.addView(myButton[i]);
			ButtonOnClickListener boc = new ButtonOnClickListener(this,SecondActivity.class,temp[0].substring(1,temp[0].length()-2),null,null,null);
			myButton[i].setOnClickListener(boc);
		}
	}
}
