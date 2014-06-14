package com.example.projectrunning;


//controlla che non ci siano corse a zero punti

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RunActivity extends Activity implements OnClickListener {

	private String provider = LocationManager.GPS_PROVIDER;
	private LocationManager locationManager;
	private LocationListener myLocationListener;


	private Button start;
	private Button stop;
	private TextView time;
	private TextView dist;
	private int counter=0;

	public ArrayList<String> places = new ArrayList<String>();


	private Location locNow;
	private Location locPre;

	private String sql;
	private String runName;

	private EditText nameField;

	private Float totDistance;
	private Float totalTime;
	private String comment;
	private String calendar;
	
	private DBAdapter adapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_run);

		//setta elementi grafici
		start = (Button) findViewById(R.id.button1);
		stop = (Button) findViewById(R.id.button2);
		time = (TextView) findViewById(R.id.textView1);
		dist = (TextView) findViewById(R.id.textView2);
		nameField = (EditText) findViewById(R.id.editText1);

		//setta a zero i contatori
		totDistance = (float) 0;
		totalTime = (float) 0;

		//setta onclicklistener
		start.setOnClickListener(this);
		stop.setOnClickListener(this);

		//adapter
		adapter = new DBAdapter(this);
		adapter.open();

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

	public void onClick(View v) {
		switch (v.getId()){
		case R.id.button1:	//START
			createName();
			Toast toast1 = Toast.makeText(getApplicationContext(), "Start pushing " + runName, Toast.LENGTH_SHORT);
			toast1.show();
			//aggiornament ricevuti ogni 3 metri o 3 secondi
			locationManager.requestLocationUpdates(provider, 3000, 3, myLocationListener);
			break;
		case R.id.button2:	//STOP
			onStopOrBack();
		}
	}

	//esegue le operazioni che devono avvenire dopo uno STOP o una pressione del tasto BACK
	private void onStopOrBack() {
		locationManager.removeUpdates(myLocationListener);
		//se non è stato raccolto alcun dato
		if(counter==0){
//			Intent iData = new Intent();
//			setResult( android.app.Activity.RESULT_CANCELED, iData );
			finish();
		}
		//se è stato raccolto almeno un dato
		else{
			setDialogComment();
		}
	}

	//metodo che crea il nome della corsa, appendendo al nome scelto la data
	private void createName() {
		runName = nameField.getText().toString() ;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		calendar = dateFormat.format(date);
		runName = "'" + runName + "_" + calendar + "'";
	}

	//metodo che raccoglie le operazioni da compiere quando ho finito una corsa con dati raccolti
	private void onFinishedRun() {
		String sql = createLastSql();
		//places.add(sql);
		adapter.execute(sql);
		Toast toast2 = Toast.makeText(getApplicationContext(), "Well done", Toast.LENGTH_SHORT);
		toast2.show();
//		Intent iData = new Intent();
//		iData.putExtra("createRun", sql);
//		setResult( android.app.Activity.RESULT_OK, iData );
		finish();

	}


	private void updateLocation(Location location){
		if (location == null)
			return;
		counter++;

		//se è il primo punto, creo l'sql e salvo la location in locNow
		if (counter==1){
			sql = createSqlFirst(location);
			locNow=location;
			//places.add(sql);
			adapter.execute(sql);
		}

		else{
			locPre = locNow;
			locNow = location;
			float distance = locNow.distanceTo(locPre);
			totDistance  = totDistance + distance;
			float time = locNow.getTime() - locPre.getTime();
			time = time/1000;
			totalTime  = totalTime + time;
			sql = createSql(locNow, locPre, counter);
			//places.add(sql);
			adapter.execute(sql);
		}

		dist.setText("Distance m : " + totDistance);
		time.setText("Time s : " + totalTime);
	}

	@Override
	public void onBackPressed() {
		onStopOrBack();
	}

	//crea la prima istruzione sql per la creazione del primo punto
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

		Toast toast = Toast.makeText(getApplicationContext(), sql, Toast.LENGTH_SHORT);
		toast.show();

		return sql;
	}

	//crea l'istruzione sql per la creazione dei punti che non sono il primo
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
		return sql;
	}

	//crea l'ultima istruzione sql per la creazione della corsa nella tabella Corse
	private String createLastSql (){
		String calApici = "'" + calendar + "'";
		String sql = String.format(
				"insert into corse (names,comment,distance,time,calendar) VALUES (%s , %s , %s , %s , %s);",
				runName, comment, totDistance, totalTime, calApici
				);
		return sql;
	}

	private void setDialogComment(){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("COMMENT");
		alertDialog.setMessage("Enter Your Comment");
		final EditText input = new EditText(this);  
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		input.setLayoutParams(lp);
		alertDialog.setView(input); 
		alertDialog.setPositiveButton("SAVE",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int which) {
				comment = "'" + input.getText().toString() + "'";
				onFinishedRun();
			}
		});


		new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		};


		alertDialog.show();
	}

}
