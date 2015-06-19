package com.example.sensortracker;

import java.util.ArrayList;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import com.example.sensortracker.code.*;
public class ThirdActivity extends ActivityWithCallBack{
	private String URL ;
	private String address;
	private String type;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		URL = getIntent().getExtras().getString("url");
		address = getIntent().getExtras().getString("address");
		Log.d("URL","from third : "+URL);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_third);
		ConnectDate connect = new ConnectDate(this,URL,address);
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
//			Log.d("input sec",URLs.get(i));
			final String[] temp = URLs.get(i).split(">");
			myButton[i] = new Button(this);
			myButton[i].setText(temp[1]);
			scrViewButLay.addView(myButton[i]);
			//			OnClickListener ocl = new OnClickListener() {			
			//				@Override
			//				public void onClick(View v) {
			//					Toast.makeText(getApplicationContext(), temp[0], Toast.LENGTH_SHORT).show();
			//				}
			//			};
			//			myButton[i].setOnClickListener(ocl);
			ButtonOnClickListener boc = new ButtonOnClickListener(this,RetreiveDataActivity.class,temp[0],temp[1],address,type);
			myButton[i].setOnClickListener(boc);				

		}
	}
	
	public void type(String type){
		this.type = type;
	}
}
