package com.example.sensortracker;

import java.util.ArrayList;

import com.example.sensortracker.code.DBHelper;

import android.app.Activity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public abstract class ActivityWithCallBack extends Activity {

	protected Button[] myButton;
	public ActivityWithCallBack() {
		super();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			DBHelper dbhelper = DBHelper.getInstance(this);
			dbhelper.clear();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void searchView(){
		Button button = (Button)findViewById(R.id.searchButton);
		EditText text = (EditText)findViewById(R.id.searhText);
		SearchOnClickListener SCL = new SearchOnClickListener(text,this);
		button.setOnClickListener(SCL);
	}
	
	public abstract void callBack(ArrayList<String> URLs) ;

	public void filter(String filter){
		for(int i =0;i<myButton.length;i++){
			if(myButton[i].getText().toString().toLowerCase().contains(filter.toLowerCase()))
				myButton[i].setVisibility(View.VISIBLE);
			else
				myButton[i].setVisibility(View.GONE);
		}
	}
}