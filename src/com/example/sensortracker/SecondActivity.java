package com.example.sensortracker;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.sensortracker.code.*;
public class SecondActivity extends Activity {
	private String URL ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		URL = getIntent().getExtras().getString("url");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_activity2);
		LinearLayout scrViewButLay = (LinearLayout)findViewById(R.id.LinearLayoutForButton);
		try {
			Connect connect = new ConnectSec(this,URL);
			connect.execute();
			ArrayList<String> URLs = connect.get();
			Button[] myButton = new Button[URLs.size()];
			for(int i = 0;i<URLs.size();i++){
				String[] temp = URLs.get(i).split(">");
				myButton[i] = new Button(this);
				myButton[i].setText(temp[1]);
				scrViewButLay.addView(myButton[i]);
				ButtonOnClickListener boc = new ButtonOnClickListener(this,ThirdActivity.class, temp[0].substring(1,temp[0].length()-2));
				myButton[i].setOnClickListener(boc);
				
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_activity2, menu);
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
}