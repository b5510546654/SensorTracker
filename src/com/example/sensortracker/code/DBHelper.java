package com.example.sensortracker.code;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	private SQLiteDatabase sqLiteDatabase;
	private static DBHelper instance;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");


	private DBHelper(Context context){
		super(context, "database.db", null, 1);
	}

	public static DBHelper getInstance(Context context) {
		if (instance == null)
			instance = new DBHelper(context);
		return instance;
	}



	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TABLE = "CREATE TABLE sensor ( id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"datetime TEXT,address TEXT,ip TEXT,type TEXT,ad0  TEXT,ad1 TEXT,ad2 TEXT,ad3 TEXT,DIO TEXT,V TEXT,TP TEXT,RSSI TEXT)";

		db.execSQL(CREATE_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String DROP_TABLE = "DROP TABLE IF EXISTS sensor";
		db.execSQL(DROP_TABLE);
		onCreate(db);
	}

	public List<Sensor> getSensor(String address,String type ,String time) {
		Log.d("sensor","get sensor");
		List<Sensor> sensors = new ArrayList<Sensor>();
		sqLiteDatabase = this.getWritableDatabase();
		String sql;
		if(time == null)
			sql = String.format("select * from sensor where sensor.address = '%s' and sensor.type = '%s'",address,type);
		else
			sql = String.format("select * from sensor where sensor.address = '%s' and sensor.type = '%s' and sensor.datetime = '%s'",address,type,time);
		Log.d("sql",sql);
		Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}

		while(!cursor.isAfterLast()) {
			//sensor attribute
			Sensor sensor = new Sensor(new Date (cursor.getString(1)), cursor.getString(2), cursor.getString(3), cursor.getString(4), 
					cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9),cursor.getString(10), cursor.getString(11),cursor.getString(12));
			sensors.add(sensor);
			
			cursor.moveToNext();
		}
		if(sensors.size() > 0)
			Log.d("sensor",sensors.get(sensors.size()-1).toString());
		sqLiteDatabase.close();

		return sensors;
	}
	
	private List<Sensor> getSensor(String address,String type ,String from,String to) {
		Log.d("sensor","get sensor");
		List<Sensor> sensors = new ArrayList<Sensor>();
		sqLiteDatabase = this.getWritableDatabase();
		String sql;
			sql = String.format("select * from sensor where sensor.address = '%s' and sensor.type = '%s' and sensor.datetime between '%s' and '%s'",address,type,from,to);
		Log.d("sql",sql);
		Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}

		while(!cursor.isAfterLast()) {
			//sensor attribute
			Sensor sensor = new Sensor(new Date (cursor.getString(1)), cursor.getString(2), cursor.getString(3), cursor.getString(4), 
					cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9),cursor.getString(10), cursor.getString(11),cursor.getString(12));
			sensors.add(sensor);
			
			cursor.moveToNext();
		}
		if(sensors.size() > 0)
			Log.d("sensor",sensors.get(sensors.size()-1).toString());
		sqLiteDatabase.close();

		return sensors;
	}
	
	public List<Sensor> getByDay(String address,String type ,String time){
		Calendar to = Calendar.getInstance();
		to.setTime(new Date(time));
		to.set(Calendar.HOUR_OF_DAY, 0);
		to.set(Calendar.MINUTE, 0);
		to.set(Calendar.SECOND, 0);
		
		Calendar from = (Calendar)to.clone();
		to.add(Calendar.DATE, 1);
		Log.d("date format",sdf.format(from.getTime()));
		return getSensor(address, type, sdf.format(from.getTime()), sdf.format(to.getTime()));
	}
	
	public List<Sensor> getByWeek(String address,String type,String time){
		Calendar to = Calendar.getInstance();
		to.setTime(new Date(time));
		to.set(Calendar.HOUR_OF_DAY, 0);
		to.set(Calendar.MINUTE, 0);
		to.set(Calendar.SECOND, 0);
		to.set(Calendar.DAY_OF_WEEK,7);
		
		Calendar from = (Calendar)to.clone();
		from.set(Calendar.DAY_OF_WEEK, 0);
		if(from.get(Calendar.DATE) >= to.get(Calendar.DATE))
			from.add(Calendar.DATE,-7);
		to.add(Calendar.DATE, 1);
		from.add(Calendar.DATE, 1);
		return getSensor(address, type, sdf.format(from.getTime()), sdf.format(to.getTime()));
	}
	public List<Sensor> getByMonth(String address,String type,String time){
		Calendar to = Calendar.getInstance();
		to.setTime(new Date(time));
		to.set(Calendar.HOUR_OF_DAY, 0);
		to.set(Calendar.MINUTE, 0);
		to.set(Calendar.SECOND, 0);
	
		Calendar from = (Calendar)to.clone();
		from.set(Calendar.DATE,1);
		to.add(Calendar.MONTH, 1);
		to.set(Calendar.DATE, 1);
		return getSensor(address, type, sdf.format(from.getTime()), sdf.format(to.getTime()));
	}

	public List<Date> containsWeek(String address,String type,String time){
		Calendar to = Calendar.getInstance();
		to.setTime(new Date(time));
		to.set(Calendar.DAY_OF_WEEK,7);
		
		Calendar from = (Calendar)to.clone();
		from.set(Calendar.DAY_OF_WEEK, 0);
		if(from.get(Calendar.DATE) >= to.get(Calendar.DATE))
			from.add(Calendar.DATE,-7);
		to.add(Calendar.DATE, 1);
		from.add(Calendar.DATE, 1);
		return getTime(address, type, sdf.format(from.getTime()), sdf.format(to.getTime()));
	}
	
	public List<Date> getTime(String address,String type ,String from,String to) {
		List<Date> times = new ArrayList<Date>();
		sqLiteDatabase = this.getWritableDatabase();
		String sql;
			sql = String.format("select sensor.datetime from sensor where sensor.address = '%s' and sensor.type = '%s' and sensor.datetime between '%s' and '%s'",address,type,from,to);
		Log.d("sql",sql);
		Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}

		while(!cursor.isAfterLast()) {
			//sensor attribute
			times.add(new Date(cursor.getString(0)));
			
			cursor.moveToNext();
		}
		sqLiteDatabase.close();

		return times;
	}
	public void addSensor(Sensor sensor){
//		Log.d("time",sdf.format(sensor.getDatetime()));
		String sql = String.format("insert into sensor (datetime,address,ip,type,ad0,ad1,ad2,ad3,DIO,V,TP,RSSI)values ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')", sdf.format(sensor.getDatetime()),sensor.getAddress(),sensor.getIp(),sensor.getType(),sensor.getAd0(),sensor.getAd1(),sensor.getAd2(),sensor.getAd3(),sensor.getDIO(),sensor.getV(),sensor.getTP(),sensor.getRSSI());
		sqLiteDatabase = this.getWritableDatabase();
//		Log.d("sensor","before add sensor");
//		Log.d("sql",sql);
		sqLiteDatabase.execSQL(sql);
	}

}
