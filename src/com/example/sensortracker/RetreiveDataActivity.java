package com.example.sensortracker;

import com.example.sensortracker.code.ConnectMain;
import com.example.sensortracker.code.Download;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RetreiveDataActivity extends Activity {
	private String URL;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		URL = getIntent().getExtras().getString("url");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_retreive_data);
		try {
			Download download = new Download(this,URL);
			download.execute();
			String str = download.get();
			TextView textView = (TextView)findViewById(R.id.TextView01);
			textView.setText(str);
		}
		catch(Exception E){
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.retreive_data, menu);
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
