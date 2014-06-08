package com.example.projectrunning;

import java.util.ArrayList;

import org.w3c.dom.Text;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RunActivity extends Activity implements OnClickListener {

	private Button start;
	private Button stop;
	private LocationListener myLocationListener;
	private int counter=0;
	private String provider = LocationManager.GPS_PROVIDER;
	public ArrayList<Location> places = new ArrayList<Location>();
	private TextView time;
	private LocationManager locationManager;
	private Thread t;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_run);

		start = (Button) findViewById(R.id.button1);
		stop = (Button) findViewById(R.id.button2);
		time = (TextView) findViewById(R.id.textView1);

		start.setOnClickListener(this);
		stop.setOnClickListener(this);
		
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		t = createTimer();


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
		case R.id.button1:
		Toast toast1 = Toast.makeText(getApplicationContext(), "Start pushing", Toast.LENGTH_SHORT);
		toast1.show();
		updateLocation(locationManager.getLastKnownLocation(provider));		//prende una prima location
		locationManager.requestLocationUpdates(provider, 5000, 3, myLocationListener);
		//startTimer();
		break;
		case R.id.button2:
			onFinishedRun();
		}
	}

	private void onFinishedRun() {
		locationManager.removeUpdates(myLocationListener);
		//stopTimer();
		Toast toast2 = Toast.makeText(getApplicationContext(), "Well done", Toast.LENGTH_SHORT);
		toast2.show();
		Intent iData = new Intent();
		iData.putParcelableArrayListExtra("locations", places);
		setResult( android.app.Activity.RESULT_OK, iData );
		finish();
	}

	private void startTimer() {
		//TODO
		t.start();
	}
	
	private void stopTimer() {
		// TODO 
		//t.
	}
	

	private Thread createTimer() {
		Thread t = new Thread() {

			private int timeCount=0;

			@Override
			public void run() {
				try {
					while (!isInterrupted()) {
						Thread.sleep(1000);
						timeCount++;

						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								time.setText(timeCount + "");			          }
						});
					}
				} catch (InterruptedException e) {
				}
			}
		};
		return t;
	}

	private void updateLocation(Location location){
		if (location == null)
			return;
		//		double lat= location.getLatitude();
		//		double lon = location.getLongitude();
		//		latitude.setText("" + lat);
		//		longitude.setText("" + lon);
		//		places.add(location);
		//		counter.setText("" + places.size());
		counter++;
		Toast toast = Toast.makeText(getApplicationContext(), "" + counter, Toast.LENGTH_SHORT);
		toast.show();
		places.add(location);
	}

	@Override
	public void onBackPressed() {
		onFinishedRun();
	}

}
