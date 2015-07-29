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
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	private SQLiteDatabase sqLiteDatabase;
	private static DBHelper instance;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private static String address;
	private static String type;
	private static String sensorName;
	private DBHelper(Context context){
		super(context, "database.db", null, 1);
	}

	public static DBHelper getInstance(Context context, String address, String type,String sensorName) {
		if (instance == null)
			instance = new DBHelper(context);
		DBHelper.address = address;
		DBHelper.type = type;
		DBHelper.sensorName =sensorName;
		return instance;
	}

	public static DBHelper getInstance(Context context) {
		if(instance == null)
			instance = new DBHelper(context);
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TABLE = "CREATE TABLE sensor (" +
				"datetime TEXT,address TEXT,type TEXT,ad0 REAL,ad1 REAL,ad2 REAL,ad3 REAL,V REAL,TP REAL,RSSI REAL,sensorName TEXT"
				+ ",UNIQUE(datetime,address,type,sensorName))";
		String CREATE_TABLE_2 = "CREATE TABLE checker (year INTEGER,month INTEGER,address TEXT,type TEXT,ccount INTEGER,sensorName TEXT,UNIQUE(year,month,address,type,sensorName))";
		db.execSQL(CREATE_TABLE);
		db.execSQL(CREATE_TABLE_2);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String DROP_TABLE = "DROP TABLE IF EXISTS sensor";
		String DROP_TABLE_2 = "DROP TABLE IF EXISTS checker";
		db.execSQL(DROP_TABLE);
		db.execSQL(DROP_TABLE_2);
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
//			dateq = "%Y-%m-%d %H:00:00";
						dateq = "%Y-%m-%d";
//			dateq = "%Y-%m-%d %H:%m:%s";
		sql = String.format("select strftime('%s',sensor.datetime) as date,avg(%s) from sensor where sensor.address = '%s' and sensor.type = '%s' and (sensor.datetime between '%s' and '%s') and sensor.sensorName = '%s' group by date",dateq,value,address,type,from.replace("/","-"),to.replace("/","-"),sensorName);
		//Log.d("sql",sql);
		Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}

		while(!cursor.isAfterLast()) {
			String z = cursor.getString(0).replace("-","/");
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
		sql = String.format("select strftime('%%Y-%%m-%%d 00:00:00',sensor.datetime) as date from sensor where sensor.address = '%s' and sensor.type = '%s' and sensor.datetime between '%s' and '%s' and sensor.sensorName = '%s' group by date",address,type,from.replace("/","-"),to.replace("/","-"),sensorName);
//		Log.d("sql",sql);
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

	public void addSensorList(List<Sensor> sensorList){
		// you can use INSERT only
		String sql = "insert OR IGNORE into sensor (datetime,address,type,ad0,ad1,ad2,ad3,V,TP,RSSI,sensorName)values (?,?,?,?,?,?,?,?,?,?,?)";	         
		SQLiteDatabase db = this.getWritableDatabase();
		db.beginTransactionNonExclusive();
		SQLiteStatement stmt = db.compileStatement(sql);

		for (Sensor sensor : sensorList) {
//			Log.d("sensor",sensor.toString());
			stmt.bindString(1, sdf.format(sensor.getDatetime()).replace("/", "-"));
			stmt.bindString(2, sensor.getAddress());
			stmt.bindString(3, sensor.getType());
			stmt.bindDouble(4, sensor.getAd0());
			stmt.bindDouble(5, sensor.getAd1());
			stmt.bindDouble(6, sensor.getAd2());
			stmt.bindDouble(7, sensor.getAd3());
			stmt.bindDouble(8, sensor.getV());
			stmt.bindDouble(9, sensor.getTP());
			stmt.bindDouble(10, sensor.getRSSI());
			stmt.bindString(11, sensorName);
			stmt.execute();
			stmt.clearBindings();
		}

		db.setTransactionSuccessful();
		db.endTransaction();

		db.close();
	}



	public void clear(){
		SQLiteDatabase db = this.getWritableDatabase();
		String DROP_TABLE = "DROP TABLE IF EXISTS sensor";
		String DROP_TABLE_2 = "DROP TABLE IF EXISTS checker";
		db.execSQL(DROP_TABLE);
		db.execSQL(DROP_TABLE_2);
		onCreate(db);
	}

	public Date maxTime(String time){
		String[] ttime = time.split("/");
		time = ttime[0]+"/"+ttime[1]+"/";
		String like = time.replace("/", "-")+"%";
		sqLiteDatabase = this.getWritableDatabase();
		String sql = String.format("select max(datetime) from sensor where sensor.address = '%s' and sensor.type = '%s' and sensor.datetime like '%s' and sensor.sensorName = '%s'",address,type,like,sensorName);
//		Log.d("sql",sql);
		Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}

		while(!cursor.isAfterLast()) {
			//sensor attribute
			sqLiteDatabase.close();
			try{
				return new Date(cursor.getString(0).replace("-", "/"));
			}
			catch(Exception e){
//				Log.d("in catch","catch");
				return null;
			}
		}
		sqLiteDatabase.close();
		return null;
	}

	public void updateFinishTable(Calendar from){
		Calendar now = Calendar.getInstance();
		if(now.get(Calendar.MONTH) > from.get(Calendar.MONTH) && now.getTimeInMillis() > from.getTimeInMillis()){
//			Log.d("update finish table","updated");
			String sql = String.format("insert OR IGNORE into checker (year,month,address,type,ccount,sensorName)values ('%s','%s','%s','%s','%s','%s')",from.get(Calendar.YEAR),from.get(Calendar.MONTH),address,type,1,sensorName);
			sqLiteDatabase = this.getWritableDatabase();
			sqLiteDatabase.execSQL(sql);
			sqLiteDatabase.close();
		}
	}

	public boolean isCompleted(Calendar from){
//		Log.d("iscompleted", "in complete");
		sqLiteDatabase = this.getWritableDatabase();
		String sql = String.format("select ccount from checker where checker.year = '%s' and checker.month = '%s' and checker.address = '%s' and checker.type = '%s' and checker.sensorName = '%s'",from.get(Calendar.YEAR),from.get(Calendar.MONTH),address,type,sensorName);
//		Log.d("sql",sql);
		Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		while(!cursor.isAfterLast()) {
//			Log.d("in while",cursor.getInt(0)+"");
			sqLiteDatabase.close();
			try{
				return cursor.getInt(0) == 1 ;
			}
			catch(Exception e){
				return false;
			}
		}
		return false;
	}
}
