package com.example.sensortracker;

import java.util.ArrayList;
import java.util.List;

import com.example.sensortracker.code.Download;
import com.example.sensortracker.code.DBHelper;
import com.example.sensortracker.code.Sensor;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class RetreiveDataActivity extends ActivityWithCallBack {
	private String URL;
	private String address;
	private String type;
	private String time;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		URL = getIntent().getExtras().getString("url");
		address = getIntent().getExtras().getString("address");
		type = getIntent().getExtras().getString("type");
		time = getIntent().getExtras().getString("time");
		Log.d("URL","from 4 : "+URL);
		super.onCreate(savedInstanceState);
		Download download = new Download(this,URL,time,address,type);
		download.execute();
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

	@Override
	public void callBack(ArrayList<String> URLs) {
		setContentView(R.layout.activity_retreive_data);
		TextView textView = (TextView)findViewById(R.id.TextView01);
		textView.setText("");
		for(int i = 0;i<URLs.size();i++){
			textView.append(URLs.get(i)+"\n");
		}
		GraphView graph = (GraphView) findViewById(R.id.graph);
		DataPoint[] datapoint = new DataPoint[URLs.size()];
		for(int i = 0;i<URLs.size();i++){
			datapoint[i] = new DataPoint(i,Double.parseDouble(URLs.get(i)));
		}
		LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(datapoint);
		graph.addSeries(series);
	}



}
