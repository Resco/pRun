package com.example.projectrunning;

import android.provider.BaseColumns;

/* DB CONTRACT */
public class DBContract {
	
	// DATABASE 
	static final String DATABASE_NAME = "brucoDB.db";
	static final int DATABASE_VERSION = 2;


	// To prevent someone from accidentally instantiating the contract class,
	// give it an empty constructor.
	private DBContract() {
	}

	/* Inner class that defines the table contents */
	static abstract class Punti implements BaseColumns {

		static final String TABLE_NAME = "Punti";
		static final String COLUMN_NAME_IDCORSA = "names";
		static final String COLUMN_NAME_NUMPUNTO = "npunto";
		static final String COLUMN_NAME_DISTANCE = "distance";
		static final String COLUMN_NAME_TIME = "time";
		static final String COLUMN_NAME_SPEED = "speed";
		static final String COLUMN_NAME_LAT = "lat";
		static final String COLUMN_NAME_LON = "lon";

		

	}
	
	static abstract class Corse implements BaseColumns {

		static final String TABLE_NAME = "Corse";
		static final String COLUMN_NAME_IDCORSA = "names";
		static final String COLUMN_NAME_COMMENT = "comment";
		static final String COLUMN_NAME_DISTANCE = "distance";
		static final String COLUMN_NAME_TIME = "time";
		static final String COLUMN_NAME_DATA = "calendar";

		

	}
}
