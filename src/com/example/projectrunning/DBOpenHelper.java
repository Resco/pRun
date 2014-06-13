package com.example.projectrunning;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

class DBOpenHelper extends SQLiteOpenHelper {

	private static final String LOG_TAG = "DBHelper";

	public DBOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	//SQL-statement per la creazione della tabella del satabase.
	private static final String SQL_CREATE_TABLE_FRASI = "create table " 
	    + DBContract.Punti.TABLE_NAME + " (" 
	    + DBContract.Punti.COLUMN_NAME_NUMPUNTO + " float," 
	    + DBContract.Punti.COLUMN_NAME_DISTANCE + " float," 
   	    + DBContract.Punti.COLUMN_NAME_TIME + " float," 
   	    + DBContract.Punti.COLUMN_NAME_SPEED + " float," 
   	    + DBContract.Punti.COLUMN_NAME_LAT + " float," 
   	    + DBContract.Punti.COLUMN_NAME_LON + " float," 
   	    + DBContract.Punti.COLUMN_NAME_IDCORSA + " varchar(100), PRIMARY KEY (" 
   	    + DBContract.Punti.COLUMN_NAME_NUMPUNTO + "," 
   	    + DBContract.Punti.COLUMN_NAME_IDCORSA + "), FOREIGN KEY ("
   	    + DBContract.Punti.COLUMN_NAME_IDCORSA + ") references " 
   	    + DBContract.Corse.TABLE_NAME + " (" 
   	    + DBContract.Corse.COLUMN_NAME_IDCORSA + ") on delete cascade"   	    
	    + ");";
	
	private static final String SQL_CREATE_TABLE_RUNS = "create table "
			+ DBContract.Corse.TABLE_NAME + " (" 
			+ DBContract.Corse.COLUMN_NAME_IDCORSA + " varchar(100)," 
		    + DBContract.Corse.COLUMN_NAME_COMMENT + " varchar(100)," 
		    + DBContract.Corse.COLUMN_NAME_DISTANCE + " float," 
	   	    + DBContract.Corse.COLUMN_NAME_TIME + " float," 
	   	    + DBContract.Corse.COLUMN_NAME_DATA + " varchar(100), PRIMARY KEY (" 
	   	    + DBContract.Corse.COLUMN_NAME_IDCORSA + ")" 
		    + ");";
			

	
	//   2 METODI
	
	
	@Override
	public void onCreate(SQLiteDatabase db) throws SQLException {
		Log.w(LOG_TAG, "Creating databse.");
		try {
			db.execSQL(SQL_CREATE_TABLE_FRASI);
			db.execSQL(SQL_CREATE_TABLE_RUNS);
		} catch (SQLException e) {
			Log.e(LOG_TAG, e.getMessage());
			throw e;
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(LOG_TAG, "Upgrading from version " + oldVersion + " to " + newVersion
		    + ", which will destroy all old data");

		// Create a new one.
		onCreate(db);
	}
}
