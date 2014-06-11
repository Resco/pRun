package com.example.projectrunning;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.w3c.dom.Text;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RunActivity extends Activity implements OnClickListener {

	private Button start;
	private Button stop;
	private LocationListener myLocationListener;
	private int counter=0;
	private String provider = LocationManager.GPS_PROVIDER;
	public ArrayList<String> places = new ArrayList<String>();
	private TextView time;
	private TextView dist;
	private LocationManager locationManager;
	private Location locNow;
	private Location locPre;
	private float distance;
	private String sql;
	private String runName;
	private EditText nameField;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_run);

		start = (Button) findViewById(R.id.button1);
		stop = (Button) findViewById(R.id.button2);
		time = (TextView) findViewById(R.id.textView1);
		dist = (TextView) findViewById(R.id.textView2);
		nameField = (EditText) findViewById(R.id.editText1);

		start.setOnClickListener(this);
		stop.setOnClickListener(this);
		
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);



		myLocationListener = new LocationListener()	{		//crea un listener

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				switch (status) {
				case LocationProvider.OUT_OF_SERVICE:
				case LocationProvider.TEMPORARILY_UNAVAILABLE:
					Toast.makeText(getApplicationContext(), "Location provider is not available.",
							Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
			}

			@Override
			public void onProviderEnabled(String provider) {
				Toast.makeText(getApplicationContext(), "Location service <" + provider + "> enabled.",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onProviderDisabled(String provider) {
				Toast.makeText(getApplicationContext(), "Location service <" + provider + "> disabled.",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onLocationChanged(Location location) {
				updateLocation(location);
			}
		};

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.button1:	//START
		createName();
		Toast toast1 = Toast.makeText(getApplicationContext(), "Start pushing " + runName, Toast.LENGTH_SHORT);
		toast1.show();
	//	updateLocation(locationManager.getLastKnownLocation(provider));		//prende una prima location
		locationManager.requestLocationUpdates(provider, 3000, 3, myLocationListener);
		break;
		case R.id.button2:	//STOP
			onFinishedRun();
		}
	}

	private void createName() {
		runName = nameField.getText().toString() ;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		runName = "'" + runName + "_" + dateFormat.format(date) + "'";
	}

	private void onFinishedRun() {
		locationManager.removeUpdates(myLocationListener);
		//stopTimer();
		Toast toast2 = Toast.makeText(getApplicationContext(), "Well done", Toast.LENGTH_SHORT);
		toast2.show();
		Intent iData = new Intent();
		iData.putStringArrayListExtra("locations", places);
		setResult( android.app.Activity.RESULT_OK, iData );
		finish();
	}



	private void updateLocation(Location location){
		if (location == null)
			return;
		counter++;
		
		if (counter==1){//se è il primo punto, creo l'sql e salvo la location in locNow
			sql = createSqlFirst(location);
			locNow=location;
			places.add(sql);
		}
		else{
			//salva le due posizioni, quella di adesso e quella di prima
		locPre = locNow;
		locNow = location;
		sql = createSql(locNow, locPre, counter);
		places.add(sql);
		}
		Toast toast = Toast.makeText(getApplicationContext(), "" + places.size() + "", Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public void onBackPressed() {
		onFinishedRun();
	}
	
	private String createSqlFirst(Location location){
		float number = 0;
		float distance = 0;
		float time = 0;
		float speed = 0;
		float lat = (float) location.getLatitude();
		float lon = (float) location.getLongitude();
		
		String sql = String.format
		("insert into punti (npunto,distance,time,speed,lat,lon,names) VALUES (%s , %s , %s , %s , %s , %s , %s );",
		 number, distance, time,speed, lat, lon, runName);
		
//		String sql = "INSERT INTO Punti (npunto,distance,time,speed,lat,lon,name) VALUES (" +
//				number + ", " +
//				distance + ", " +
//				time + ", " +
//				speed + ", " +
//				lat + ", " +
//				lon + ", " +
//				runName +");";
		Toast toast = Toast.makeText(getApplicationContext(), sql, Toast.LENGTH_SHORT);
		toast.show();
		dist.setText(sql);

		return sql;
		
	}
	
	private String createSql(Location locationNow, Location locationPre, int num){
		//devo calcolare speed
		float number = num;
		float distance = locationNow.distanceTo(locationPre);
		float time = locationNow.getTime() - locationPre.getTime();
		time = time/1000;
		float speed = time/distance;
		float lat = (float) locationNow.getLatitude();
		float lon = (float) locationNow.getLongitude();
		
		String sql = String.format
		("insert into punti (npunto,distance,time,speed,lat,lon,names) VALUES (%s , %s , %s , %s , %s , %s , %s );",
		 number, distance, time,speed, lat, lon, runName);
		
		Toast toast = Toast.makeText(getApplicationContext(), sql, Toast.LENGTH_SHORT);
		toast.show();
		dist.setText(sql);
		return sql;
		
	}

}
