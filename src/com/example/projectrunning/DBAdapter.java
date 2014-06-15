package com.example.projectrunning;

import java.io.ByteArrayOutputStream;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.util.Log;

class DBAdapter {

	// the tag used in LogCat messages
	private static String LOG_TAG = "DBAdapter";

	// ADAPTER STATE
	private SQLiteDatabase db; // reference to the DB
	private DBOpenHelper dbHelper; // reference to the OpenHelper

	
	//CREATORE
	//crea una istanza di DBHelper
	public DBAdapter(Context context) {
		this.dbHelper = new DBOpenHelper(context, DBContract.DATABASE_NAME, null,
		    DBContract.DATABASE_VERSION);
	}

	/**
	 * Open the DB in write mode. If the DB cannot be opened in write mode, the
	 * method throws an exception to signal the failure
	 * @return this (self reference, allowing this to be chained in an
	 * initialization call)
	 * @throws SQLException if the database could be neither opened or created
	 */
	public DBAdapter open() throws SQLException {
		try {
			db = dbHelper.getWritableDatabase();
		} catch (SQLiteException e) {
			Log.e(LOG_TAG, e.getMessage());
			throw e;
		}
		return this;
	}

	
	/**
	 * Close the DB. 
	 */
	public void close() {
		db.close();
	}

	/**
	 * @HINT
	 * 
	 * Add the specified element to the DB.
	 * @param todoItem 
	 * @return @return rowId or -1 if failed
	 */
	public void execute(String sql){
		db.execSQL(sql);
	}
	
	public int numberOfRows(){
		Cursor cursore = this.getAllEntriesPunti();
		int contatore = cursore.getCount();
		return contatore;
	}
	
//	public long insert(String phrase) {
//		Log.w(LOG_TAG, "Adding todoItem to the database.");
//		ContentValues values = new ContentValues();
//		values.put(DBContract.Frasi.COLUMN_NAME_PHRASE, phrase);
//		long idx = db.insert(DBContract.Frasi.TABLE_NAME, null, values);
//		Log.w(LOG_TAG, "Added value idx: " + idx);
//		db.e
//		return idx;
//	}

	/**
   * Delete the TodoItem with the the given rowId.
   * 
   * @param rowId
   * @return true if deleted, false otherwise.
   */
//	public boolean deleteFrase(long idx) {
//		return db.delete(DBContract.Frasi.TABLE_NAME, DBContract.Frasi._ID + "=" + idx, null) > 0;
//	}
//	
	public void deleteAllRows(){
		db.execSQL("DELETE FROM " +DBContract.Punti.TABLE_NAME +" WHERE " + DBContract.Punti.COLUMN_NAME_NUMPUNTO + " >=0");
	}
	

//
//	/**
//   * Return a Cursor over the list of all items in the database
//   * 
//   * @return Cursor over all todoItems 
//   */
	public Cursor getAllEntriesPunti() {
		return db.query(DBContract.Punti.TABLE_NAME, null, null, null, null, null, null);
	}
	
	public Cursor getAllEntriesCorse() {
		return db.query(DBContract.Corse.TABLE_NAME, null, null, null, null, null, null);
	}
	
	public Cursor getAllRunsName(){
		String [] colonne = {DBContract.Corse.COLUMN_NAME_IDCORSA};
		return db.query(DBContract.Corse.TABLE_NAME, colonne, null, null, null, null, null);
	}
	
	public Cursor getIdRowFromCorse (String id){
		Cursor cursor = db.query(true, "corse", null, "names = " + id , null, null, null, null, null);
		return cursor;
	}

	public Cursor getIdRowFromPunti(String id) {
		Cursor cursor = db.query(true, "punti", null, "names = " + id , null, null, null, null, null);
		return cursor;
	}

}