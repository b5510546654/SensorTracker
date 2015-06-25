package com.example.sensortracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.sensortracker.code.*;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
public class ThirdActivity extends ActivityWithCallBack{
	private String URL ;
	private String address;

	private TextView text_date;
	private DatePicker date_picker;
	private Button button;
	private Button buttonGetData;

	private int year;
	private int month;
	private int day;

	static final int DATE_DIALOG_ID = 100;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		URL = getIntent().getExtras().getString("url");
		address = getIntent().getExtras().getString("address");
		Log.d("URL","from third : "+URL);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_third);
		//		ConnectDate connect = new ConnectDate(this,URL,address);
		//		connect.execute();

		setCurrentDate();
		addButtonListener();
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
//		setContentView(R.layout.activity_third);
		//		TextView textView = (TextView)findViewById(R.id.TextView01);
		//		textView.setText("");
		//		for(int i = 0;i<sensors.size();i++){
		//			textView.append(sensors.get(i)+"\n");
		//		}
		GraphView graph = (GraphView) findViewById(R.id.graph);
		graph.removeAllSeries();
		DataPoint[] datapoint = new DataPoint[URLs.size()];
		Date min = new Date();
		Date max = new Date();
		for(int i = 0;i<URLs.size();i++){
			String [] temp = URLs.get(i).split(">");
			Date date = new Date(temp[0]);
			datapoint[i] = new DataPoint(new Date(temp[0]),Double.parseDouble(temp[1]));
			if(min.compareTo(date) > 0)
				min = date;
			if(max.compareTo(date) < 0)
				max = date;
			
		}
		LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(datapoint);
		graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
//		graph.getGridLabelRenderer().resetStyles();
		graph.getViewport().setMinX(min.getTime());
		graph.getViewport().setMaxX(max.getTime());
		graph.getViewport().setXAxisBoundsManual(true);


		graph.addSeries(series);
		graph.setVisibility(GraphView.VISIBLE);
		
		
		
		
		//		LinearLayout scrViewButLay = (LinearLayout)findViewById(R.id.LinearLayoutForButton);
		//		//		if(filter != null){
		//		//			URLs = filter(URLs);
		//		//		}
		//		myButton = new Button[URLs.size()];
		//		for(int i = 0;i<URLs.size();i++){
		//			//			Log.d("input sec",URLs.get(i));
		//			final String[] temp = URLs.get(i).split(">");
		//			myButton[i] = new Button(this);
		//			myButton[i].setText(temp[1]);
		//			scrViewButLay.addView(myButton[i]);
		//			ButtonOnClickListener boc = new ButtonOnClickListener(this,RetreiveDataActivity.class,temp[0],temp[1],address,type);
		//			myButton[i].setOnClickListener(boc);	
	}


	// display current date both on the text view and the Date Picker when the application starts.
	public void setCurrentDate() {

		text_date = (TextView) findViewById(R.id.dateText);

		final Calendar calendar = Calendar.getInstance();

		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);

		// set current date into textview
		text_date.setText(new StringBuilder()
		// Month is 0 based, so you have to add 1
		.append(month + 1).append("-")
		.append(day).append("-")
		.append(year).append(" "));

		// set current date into Date Picker
		//date_picker.init(year, month, day, null);

	}

	public void addButtonListener() {

		button = (Button) findViewById(R.id.buttonDate);

		button.setOnClickListener(new ButtonOnClickShowDatePicker());

		buttonGetData = (Button) findViewById(R.id.buttonGetData);

		buttonGetData.setOnClickListener(new ButtonOnClickGetData(this));
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case DATE_DIALOG_ID:
			// set date picker as current date
			return new DatePickerDialog(this, datePickerListener, year, month,day);
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear,int selectedMonth, int selectedDay) {
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;

			// set selected date into Text View
			text_date.setText(new StringBuilder().append(month + 1)
					.append("-").append(day).append("-").append(year).append(" "));

			// set selected date into Date Picker
			//date_picker.init(year, month, day, null);

		}
	};

	class ButtonOnClickShowDatePicker implements OnClickListener{

		@Override
		public void onClick(View v) {
			showDialog(DATE_DIALOG_ID);	
		}

	}

	class ButtonOnClickGetData implements OnClickListener{

		private ActivityWithCallBack activityWithCallBack;

		public ButtonOnClickGetData(ActivityWithCallBack activityWithCallBack) {
			this.activityWithCallBack = activityWithCallBack;
		}

		@Override
		public void onClick(View v) {
			String strMonth = String.format("%02d",month+1);
			String date = year+"/"+strMonth+"/"+day+" 00:00:00";
			String value = ((Spinner)findViewById(R.id.spinnerValue)).getSelectedItem().toString();
			String period = ((Spinner)findViewById(R.id.spinnerPeriod)).getSelectedItem().toString();
			Download download = new Download(activityWithCallBack, URL, date,value,period, address);
			download.execute();
		}

	}
	
	public static double parseDoubleSafely(String str) {
	    double result = 0;
	    try {
	        result = Double.parseDouble(str);
	    } catch (NullPointerException npe) {
	    } catch (NumberFormatException nfe) {
	    }
	    return result;
	}

}
