package com.example.sensortracker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sensortracker.controller.Download;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
public class ThirdActivity extends ActivityWithCallBack{
	private String URL ;
	private String address;
	private Date lastUpdate;
	private String type;

	private TextView text_date;
	private Button button;
	private Button buttonGetData;

	private int year;
	private int month;
	private int day;
	static final int DATE_DIALOG_ID = 100;
	private String period;
	private Calendar calendar;
	private String sensorName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		URL = getIntent().getExtras().getString("url");
		address = getIntent().getExtras().getString("address");
		type = getIntent().getExtras().getString("type");
		lastUpdate = new Date(getIntent().getExtras().getString("lastUpdate"));
		sensorName = getIntent().getExtras().getString("text");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_third);
		TextView textView = (TextView)findViewById(R.id.TextView01);
		textView.setText("Sensor ID : "+sensorName);
		setCurrentDate();
		addButtonListener();

		
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;
		LinearLayout rLGreen = ((LinearLayout) findViewById(R.id.graphLayout));
		rLGreen.setLayoutParams(new LinearLayout.LayoutParams((int) (height*0.6), width));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void callBack(ArrayList<String> URLs) {
		GraphView graph = (GraphView) findViewById(R.id.graph);
		graph.removeAllSeries();
		if(URLs.isEmpty()){
			graph.setVisibility(GraphView.INVISIBLE);
			noData();
			return;
		}
		if(URLs.size() == 1)
			URLs.add(URLs.get(0));
		
		DataPoint[] datapoint = new DataPoint[URLs.size()];
		List<Date> dates = new ArrayList<Date>();
		List<DataPoint[]> datapoints = new ArrayList<DataPoint[]>();
		Date before = null;
		List<Integer> counter = new ArrayList<Integer>();
		counter.add(0);
		int runner = 0;
		for(int i = 0;i<URLs.size();i++){
			String [] temp = URLs.get(i).split(">");
			Date date = new Date(temp[0]);
			if(before != null && date.getTime() - before.getTime() > 24*60*60*1000){
				counter.add(0);
				runner++;
				//				Log.d("runner",runner+"");
			}
			counter.set(runner, counter.get(runner)+1);
			before = date;
		}
		List<Double> yValues = new ArrayList<Double>();
		for(int i = 0;i<URLs.size();i++){
			String [] temp = URLs.get(i).split(">");
			Date date = new Date(temp[0]);
			datapoint[i] = new DataPoint(date,Double.parseDouble(temp[1]));
			dates.add(date);
			yValues.add(Double.parseDouble(temp[1]));
		}

		int count = counter.get(0);
		int a = 0;
		int b = 1;
		DataPoint[] dtemp = new DataPoint[count];
		for(int i =0;i<URLs.size();i++){
			if(a < count){
				dtemp[a] = new DataPoint(datapoint[i].getX(), datapoint[i].getY());
				a++;
			}
			else{
				datapoints.add(dtemp);
				count = counter.get(b);
				dtemp = new DataPoint[count];
				a = 0;
				b++;
				i--;
			}
			if(i == URLs.size()-1)
				datapoints.add(dtemp);
		}


		graph.getViewport().setMinX(Collections.min(dates).getTime());
		graph.getViewport().setMaxX(Collections.max(dates).getTime());
		graph.getViewport().setXAxisBoundsManual(true);
		graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
			@Override
			public String formatLabel(double value,boolean isValueX){
				if(isValueX)
					return chooseDateFormat().format(new Date((long) value));
				else
					return super.formatLabel(value, isValueX);//value+"";
			}

		});

		for(int i = 0;i<datapoints.size();i++){
			LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(datapoints.get(i));
			series.setOnDataPointTapListener(new OnDataPointTapListener() {

				@Override
				public void onTap(Series arg0, DataPointInterface arg1) {
					String x = new SimpleDateFormat("yyyy/MM/dd HH").format(new Date((long) arg1.getX()));
					String y = String.format("%.2f", arg1.getY());
					Toast.makeText(ThirdActivity.this, "Data Point\nDate  : "+x+"\nValue : "+y, Toast.LENGTH_SHORT).show();					
				}
			}); 
			graph.addSeries(series);
		}
		graph.getGridLabelRenderer().setLabelsSpace(20);
		graph.getViewport().setScrollable(true);
//		graph.getViewport().setScalable(true);
		graph.setVisibility(GraphView.VISIBLE);
//		Log.d("Time",Calendar.getInstance().getTimeInMillis() - calendar.getTimeInMillis()+"");
	}

	private void noData() {
		Toast.makeText(getApplicationContext(), "No data available. or Connection lost.",
				   Toast.LENGTH_LONG).show();
	}

	public SimpleDateFormat chooseDateFormat(){
		if(period.equals("Week"))
			return new SimpleDateFormat("yyyy/MM/dd\nHH");
		else if(period.equals("Month"))
			return new SimpleDateFormat("yyyy/MM/dd");
		else
			return new SimpleDateFormat("HH");
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
			String strDay = String.format("%02d",day);
			String date = year+"/"+strMonth+"/"+strDay+" 00:00:00";
			String value = ((Spinner)findViewById(R.id.spinnerValue)).getSelectedItem().toString();
			period = ((Spinner)findViewById(R.id.spinnerPeriod)).getSelectedItem().toString();
			Download download = new Download(activityWithCallBack, URL, date,value,period, address,type,lastUpdate,sensorName);
			download.execute();
			calendar = Calendar.getInstance();
		}

	}

}
