package com.example.projectrunning;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
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
	public ArrayList<Location> places = new ArrayList<Location>();
	private int RUN_CODE = 1;
	private TextView conta;



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
	private void updateLocation(Location location){
		if (location == null)
			return;
		double lat= location.getLatitude();
		double lon = location.getLongitude();
		latitude.setText("" + lat);
		longitude.setText("" + lon);
		places.add(location);
		counter.setText("" + places.size());
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
		intentMap.putParcelableArrayListExtra("array", places);
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
            	 places = pData.getParcelableArrayListExtra("locations");
            	 conta.setText(places.size() + "");
                 
             }
         }
	 }

}
