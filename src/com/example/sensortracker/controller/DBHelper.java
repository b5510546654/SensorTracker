package com.example.sensortracker.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.example.sensortracker.model.ReturnObject;
import com.example.sensortracker.model.Sensor;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	private SQLiteDatabase sqLiteDatabase;
	private static DBHelper instance;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private static String address;
	private static String type;

	private DBHelper(Context context){
		super(context, "database.db", null, 1);
	}

	public static DBHelper getInstance(Context context, String address, String type) {
		if (instance == null)
			instance = new DBHelper(context);
		DBHelper.address = address;
		DBHelper.type = type;
		return instance;
	}

	public static DBHelper getInstance(Context context) {
		if(instance == null)
			instance = new DBHelper(context);
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TABLE = "CREATE TABLE sensor ( id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"datetime TEXT,address TEXT,ip TEXT,type TEXT,ad0  REAL,ad1 REAL,ad2 REAL,ad3 REAL,DIO REAL,V REAL,TP REAL,RSSI REAL)";

		db.execSQL(CREATE_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String DROP_TABLE = "DROP TABLE IF EXISTS sensor";
		db.execSQL(DROP_TABLE);
		onCreate(db);
	}

	private List<ReturnObject> getResult(String value,String from,String to,String str) {
		List<ReturnObject> ros = new ArrayList<ReturnObject>();
		sqLiteDatabase = this.getWritableDatabase();
		String sql;
		String dateq = "";
		if(str.equals("day"))
						dateq = "%Y-%m-%d %H:%m:00";
//			dateq = "%Y-%m-%d %H:%m:%S";

		else if(str.equals("week"))
						dateq = "%Y-%m-%d %H:%m:00";
//			dateq = "%Y-%m-%d %H:%m:%s";

		else
						dateq = "%Y-%m-%d";
//			dateq = "%Y-%m-%d %H:%m:%s";

		sql = String.format("select strftime('%s',sensor.datetime) as date,avg(%s) from sensor where sensor.address = '%s' and sensor.type = '%s' and (sensor.datetime between '%s' and '%s') group by date",dateq,value,address,type,from.replace("/","-"),to.replace("/","-"));
		//Log.d("sql",sql);
		Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}

		while(!cursor.isAfterLast()) {
//			Log.d("cursor",cursor.getString(0));
//			Log.d("cursor",cursor.getDouble(1)+"");
			String z = cursor.getString(0).replace("-","/");
			double y = cursor.getDouble(1);
			ReturnObject ro = new ReturnObject(new Date(z), cursor.getDouble(1));
			ros.add(ro);
			cursor.moveToNext();
		}
		sqLiteDatabase.close();

		return ros;
	}

	public List<ReturnObject> getByDay(String value,String time){
		Calendar to = Calendar.getInstance();
		to.setTime(new Date(time));
		Calendar from = (Calendar)to.clone();
		to.add(Calendar.DATE, 1);
		return getResult(value,sdf.format(from.getTime()), sdf.format(to.getTime()),"day");
	}

	public List<ReturnObject> getByWeek(String value,String time){
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
		return getResult(value,sdf.format(from.getTime()), sdf.format(to.getTime()),"week");
	}
	public List<ReturnObject> getByMonth(String value,String time){
		Calendar to = Calendar.getInstance();
		to.setTime(new Date(time));
		to.set(Calendar.HOUR_OF_DAY, 0);
		to.set(Calendar.MINUTE, 0);
		to.set(Calendar.SECOND, 0);

		Calendar from = (Calendar)to.clone();
		from.set(Calendar.DATE,1);
		to.add(Calendar.MONTH, 1);
		to.set(Calendar.DATE, 1);
		return getResult(value,sdf.format(from.getTime()), sdf.format(to.getTime()),"month");
	}

	public List<Date> getTime(String from,String to) {
		List<Date> times = new ArrayList<Date>();
		sqLiteDatabase = this.getWritableDatabase();
		String sql;
		sql = String.format("select strftime('%%Y-%%m-%%d 00:00:00',sensor.datetime) as date from sensor where sensor.address = '%s' and sensor.type = '%s' and sensor.datetime between '%s' and '%s' group by date",address,type,from.replace("/","-"),to.replace("/","-"));
		//Log.d("sql",sql);
		Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}

		while(!cursor.isAfterLast()) {
			//sensor attribute
			times.add(new Date(cursor.getString(0).replace("-", "/")));

			cursor.moveToNext();
		}
		sqLiteDatabase.close();

		return times;
	}
	public void addSensor(Sensor sensor){
		//Log.d("Sensor",sensor.toString());
		String sql = String.format("insert into sensor (datetime,address,ip,type,ad0,ad1,ad2,ad3,DIO,V,TP,RSSI)values ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')", sdf.format(sensor.getDatetime()).replace("/", "-"),sensor.getAddress(),sensor.getIp(),sensor.getType(),sensor.getAd0(),sensor.getAd1(),sensor.getAd2(),sensor.getAd3(),sensor.getDIO(),sensor.getV(),sensor.getTP(),sensor.getRSSI());
		sqLiteDatabase = this.getWritableDatabase();
		sqLiteDatabase.execSQL(sql);
	}

	public void clear(){
		SQLiteDatabase db = this.getWritableDatabase();
		String DROP_TABLE = "DROP TABLE IF EXISTS sensor";
		db.execSQL(DROP_TABLE);
		onCreate(db);
	}

	public Date maxTime(String time){
		String like = time.replace("/", "-")+"%";
		sqLiteDatabase = this.getWritableDatabase();
		String sql = String.format("select max(datetime) from sensor where sensor.address = '%s' and sensor.type = '%s' and sensor.datetime like '%s'",address,type,like);
		//		Log.d("sql",sql);
		Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}

		while(!cursor.isAfterLast()) {
			//sensor attribute
			//			Log.d("cursor",cursor.getString(0).replace("-", "/"));
			sqLiteDatabase.close();
			return new Date(cursor.getString(0).replace("-", "/"));
		}
		sqLiteDatabase.close();
		return null;
	}

	public void deleteTime(String time){
		sqLiteDatabase = this.getWritableDatabase();
		String like = time.replace("/", "-")+"%";
		String sql = String.format("delete from sensor where sensor.address = '%s' and sensor.type = '%s' and sensor.datetime like '%s'",address,type,like);
		sqLiteDatabase.execSQL(sql);
	}
}
