package com.example.sensortracker;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.sensortracker.controller.DBHelper;

public abstract class ActivityWithCallBack extends Activity {

	protected Button[] myButton;
	public ActivityWithCallBack() {
		super();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Do you really want to clear cache?").setTitle("Confirmation");
			builder.setIcon(android.R.drawable.ic_dialog_alert);
			builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

			    public void onClick(DialogInterface dialog, int whichButton) {
//			        Toast.makeText(ActivityWithCallBack.this, "Yaay", Toast.LENGTH_SHORT).show();
					DBHelper dbhelper = DBHelper.getInstance(ActivityWithCallBack.this);
					dbhelper.clear();
			    }}).setNegativeButton(android.R.string.no, null).show();
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