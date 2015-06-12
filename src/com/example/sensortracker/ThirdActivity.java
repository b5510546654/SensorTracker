package com.example.sensortracker;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.sensortracker.code.*;
public class ThirdActivity extends Activity {
	private String URL ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		URL = getIntent().getExtras().getString("url");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_third);
		LinearLayout scrViewButLay = (LinearLayout)findViewById(R.id.LinearLayoutForButton);
		try {
			ConnectDate connect = new ConnectDate(this,URL);
			connect.execute();
			ArrayList<String> URLs = connect.get();
			Button[] myButton = new Button[URLs.size()];
			for(int i = 0;i<URLs.size();i++){
				Log.d("input sec",URLs.get(i));
				final String[] temp = URLs.get(i).split(">");
				myButton[i] = new Button(this);
				myButton[i].setText(temp[1]);
				scrViewButLay.addView(myButton[i]);
//				OnClickListener ocl = new OnClickListener() {			
//					@Override
//					public void onClick(View v) {
//						Toast.makeText(getApplicationContext(), temp[0], Toast.LENGTH_SHORT).show();
//					}
//				};
//				myButton[i].setOnClickListener(ocl);
				ButtonOnClickListener boc = new ButtonOnClickListener(this,RetreiveDataActivity.class,temp[0]);
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
}
