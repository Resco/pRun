package com.example.projectrunning;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private TextView latitude;
	private TextView longitude;
	private TextView counter;
	private Button map;
	private Button run;
	private LocationListener myLocationListener;
	private String provider = LocationManager.GPS_PROVIDER;
	public ArrayList<String> places = new ArrayList<String>();
	private int RUN_CODE = 1;
	private TextView conta;
	private DBAdapter dbAdapter;
	private DBAdapter adapter;
	private String comando;
	private Cursor cursor;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		latitude = (TextView) findViewById(R.id.textView2);
		longitude = (TextView) findViewById(R.id.textView4);
		conta = (TextView) findViewById(R.id.textView5);
		counter = (TextView) findViewById(R.id.placesCounter);
		map = (Button) findViewById(R.id.button1);
		run = (Button) findViewById(R.id.button2);

		map.setOnClickListener(this);
		run.setOnClickListener(this);

		adapter = new DBAdapter(this);
		adapter.open();
		
//		float number = 0;
//		float distance = 0;
//		float time = 0;
//		float speed = 0;
//		float lat = (float) 45.6532;
//		float lon = (float) 45.6532;
//		String runName = "'minchiaz'";
//		String sql = String.format
//				("insert into Punti (npunto,distance,time,speed,lat,lon,names) VALUES (%s , %s , %s , %s , %s , %s , %s );",
//				 number, distance, time, speed, lat, lon, runName);
//		adapter.execute(sql);
//		
//		String sql = "INSERT INTO Punti (npunto,distance,time,speed,lat,lon,name) VALUES (" +
//				number + ", " +
//				distance + ", " +
//				time + ", " +
//				speed + ", " +
//				lat + ", " +
//				lon + ", " +
//				runName +");";
		
		
//		adapter.deleteAllRows();
		

		
		cursor = adapter.getAllEntries();
		
		conta.setText(cursor.getCount() + "");
//		adapter.deleteAllRows();


	}
	

	protected void onResume() {			//è chiamato dopo una sospensione della app, o anche dopo il primo OnCreate
		super.onResume();
//		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//		updateLocation(locationManager.getLastKnownLocation(provider));		//prende una prima location
//		locationManager.requestLocationUpdates(provider, 5000, 3, myLocationListener);

	}

	protected void onPause() {
//		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//		locationManager.removeUpdates(myLocationListener);
	super.onPause();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.button1:
		Intent intentMap = new Intent(this, MapActivity.class); // Explicit intent creation
		//intentMap.putParcelableArrayListExtra("array", places);
		startActivityForResult(intentMap, 1); // Start as sub-activity for result
		break;
		case R.id.button2:
			Intent intentRun = new Intent(this, RunActivity.class); // Explicit intent creation
			startActivityForResult(intentRun, RUN_CODE); // Start as sub-activity for result	
		}
	}
	
	 protected void onActivityResult(
	            int requestCode, 
	            int resultCode,
	            Intent pData) {
		 if ( requestCode == RUN_CODE ) 
         {
             if (resultCode == Activity.RESULT_OK ) 
             {
            	 places = pData.getStringArrayListExtra("locations");
            	 conta.setText(places.size() + "");
            	 for (int i=0; i<places.size(); i++){
            		 comando = places.get(i);
            		 adapter.execute(comando);
            	 }
            	
             }
         }
	 }

}
