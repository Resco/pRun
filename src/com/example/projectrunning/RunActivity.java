package com.example.projectrunning;

//TODO
//controlla che non ci siano corse a zero punti

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
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
	private Button rerun;
	private TextView rerunName;
	private TextView time;
	private TextView dist;

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
	private int counter=0;
	private boolean rerunBool = false;
	private float [] checkPoints = new float[4];
	private float [] checkTimes = new float [4];


	private DBAdapter adapter;
	private float speed;
	private int checkdone = 0;
	private TextView delay;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_run);

		//setta elementi grafici
		start = (Button) findViewById(R.id.button1);
		stop = (Button) findViewById(R.id.button2);
		rerun = (Button) findViewById(R.id.button3);
		time = (TextView) findViewById(R.id.textView1);
		rerunName = (TextView) findViewById(R.id.textView3);
		delay = (TextView) findViewById(R.id.textView4);
		dist = (TextView) findViewById(R.id.textView2);
		nameField = (EditText) findViewById(R.id.editText1);

		//setta a zero i contatori
		totDistance = (float) 0;
		totalTime = (float) 0;
		checkdone = 0;

		//setta onclicklistener
		start.setOnClickListener(this);
		stop.setOnClickListener(this);
		rerun.setOnClickListener(this);

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
			break;
		case R.id.button3:
			Cursor cursor = adapter.getAllRunsName();
			String[] nameRun = new String [cursor.getCount()];
			int i=0;
			if (cursor.moveToFirst()){
				do{
					nameRun [i] = cursor.getString(0);
					i++;
				}while(cursor.moveToNext());
			}
			cursor.close();
			createListDialog(nameRun, "BelTitolo");
			break;
		}
	}

	public void onBackPressed() {
		onStopOrBack();
	}

	//esegue le operazioni che devono avvenire dopo uno STOP o una pressione del tasto BACK
	private void onStopOrBack() {
		locationManager.removeUpdates(myLocationListener);
		//se non è stato raccolto alcun dato
		if(counter==0){
			finish();
		}
		//se è stato raccolto almeno un dato
		else{
			setDialogComment();
		}
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

	//metodo che raccoglie le operazioni da compiere quando ho finito una corsa con dati raccolti
	private void onFinishedRun() {
		String sql = createLastSql();
		adapter.execute(sql);
		Toast toast2 = Toast.makeText(getApplicationContext(), "Well done", Toast.LENGTH_SHORT);
		toast2.show();
		finish();

	}

	//metodo che crea il nome della corsa, appendendo al nome scelto la data
	@SuppressLint("SimpleDateFormat")
	private void createName() {
		runName = nameField.getText().toString() ;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		calendar = dateFormat.format(date);
		runName = "'" + runName + "_" + calendar + "'";
	}

	private void createListDialog(String [] items, String title ) {
		final String[] MenuItems = items;
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		// set title
		alertDialogBuilder.setTitle(title);
		// set dialog message
		alertDialogBuilder
		.setCancelable(true)
		.setSingleChoiceItems(MenuItems, 0, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog1, int pos) {

				for (int i = 0; i<MenuItems.length; i++){
					if (i==pos){
						//ciò che voglio fare quando clicco un elemento
						Toast toast = Toast.makeText(getApplicationContext(), MenuItems[i] + pos, Toast.LENGTH_SHORT);
						toast.show();
						String selected = "'" + MenuItems[i] +"'";
						rerunName.setText(selected);
						//TODO in asynctask
						//segnalo che sono in rerun
						rerunBool  = true;

						//gli array dove salvo i check (Time e distance)


						Cursor cursor = adapter.getIdRowFromPunti(selected);
						cursor.moveToFirst();

						//approssimazione per difetto al naturale più vicino ad 1/4 del numero dei punti del percorso
						int numbRows = cursor.getCount();
						int x = numbRows/4;
						int xRounded = 0;
						for (int j = 0; j< numbRows; j++){
							if (j>x){
								xRounded=j-1;
								break;}
						}

						createCheckpoints(cursor, numbRows, xRounded);

					}
				}

				//			        	
				dialog1.cancel();
			}

			private void createCheckpoints(Cursor cursor, int numbRows,
					int xRounded) {
				float distCounter;
				float lastDist;
				float timeCounter;
				float lastTime;
				//salva i 4 check con tempo e distanza di ognuno
				for (int y = 1; y<=3; y++){
					distCounter=0;
					timeCounter=0;
					for (int z = 0; z<=(xRounded*y)-1; z++){
						cursor.moveToPosition(z);
						lastDist = cursor.getFloat(cursor.getColumnIndex(DBContract.Punti.COLUMN_NAME_DISTANCE));
						distCounter = distCounter + lastDist;
						lastTime = cursor.getFloat(cursor.getColumnIndex(DBContract.Punti.COLUMN_NAME_TIME));
						timeCounter = timeCounter + lastTime;

					}
					checkPoints[y-1]=distCounter;
					checkTimes[y-1]=timeCounter;
					Toast.makeText(getApplicationContext(), "" + distCounter + " " + timeCounter,
							Toast.LENGTH_SHORT).show();
				}
				distCounter=0;
				timeCounter=0;
				for (int z = 0; z<=(numbRows -1); z++){
					cursor.moveToPosition(z);
					lastDist = cursor.getFloat(cursor.getColumnIndex(DBContract.Punti.COLUMN_NAME_DISTANCE));
					distCounter = distCounter + lastDist;
					lastTime = cursor.getFloat(cursor.getColumnIndex(DBContract.Punti.COLUMN_NAME_TIME));
					timeCounter = timeCounter + lastTime;

				}
				checkPoints[3]=distCounter;
				checkTimes[3]=timeCounter;
				Toast.makeText(getApplicationContext(), "" + distCounter + " " + timeCounter,
						Toast.LENGTH_SHORT).show();
			}
		})
		.setPositiveButton("Cancel",new DialogInterface.OnClickListener() {
			@SuppressLint("NewApi")
			public void onClick(DialogInterface dialog1,int id) {
				dialog1.cancel();
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// show it
		alertDialog.show();
	}

	private void updateLocation(Location location){
		if (location == null)
			return;
		counter++;

		//se è il primo punto, creo l'sql e salvo la location in locNow
		if (counter==1){
			sql = createSqlFirst(location);
			locNow=location;
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
			adapter.execute(sql);
			if (rerunBool){


				switch (checkdone){
				case 0:
					compareTime(checkdone);
					Toast.makeText(getApplicationContext(), "enter in case " + checkdone,
							Toast.LENGTH_SHORT).show();
					break;
				case 1:
					compareTime(checkdone);
					Toast.makeText(getApplicationContext(), "enter in case " + checkdone,
							Toast.LENGTH_SHORT).show();
					break;
				case 2:
					compareTime(checkdone);
					Toast.makeText(getApplicationContext(), "enter in case " + checkdone,
							Toast.LENGTH_SHORT).show();
					break;
				case 3:
					compareTime(checkdone);
					Toast.makeText(getApplicationContext(), "enter in case " + checkdone,
							Toast.LENGTH_SHORT).show();
					break;
				}

			}
		}

		dist.setText("Distance m : " + totDistance);
		time.setText("Time s : " + totalTime);
	}

	private void compareTime(int checkdone) {
		String delayString;
		float difference = 0;

		if(totDistance>checkPoints[checkdone]){
			float diff = totDistance-checkPoints[checkdone];	//metri in più rispetto al check
			float timeDiff = diff * speed;						//il tempo impiegato per fare i metri in più
			float timeToCompare = totalTime - timeDiff;			//il tempo impiegato a fare gli stessi metri del check
			if (timeToCompare>checkTimes[checkdone]){
				//				Toast.makeText(getApplicationContext(), "Sei stato più lento",
				//				Toast.LENGTH_SHORT).show();
				difference = timeToCompare - checkTimes[checkdone];
				delayString="Hai percorso " + checkPoints[checkdone] +" metri PIU' LENTAMENTE di " + difference + " secondi.";
				delay.setText(delayString);}
			if (timeToCompare<=checkTimes[checkdone]){
				//				Toast.makeText(getApplicationContext(), "Sei stato più veloce",
				//				Toast.LENGTH_SHORT).show();		
				difference = checkTimes[checkdone] - timeToCompare;
				delayString="Hai percorso " + checkPoints[checkdone] +" metri PIU' VELOCEMENTE di " + difference + " secondi.";
				delay.setText(delayString);}
			this.checkdone++;
		}
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

		return sql;
	}

	//crea l'istruzione sql per la creazione dei punti che non sono il primo
	private String createSql(Location locationNow, Location locationPre, int num){
		//devo calcolare speed
		float number = num;
		float distance = locationNow.distanceTo(locationPre);
		float time = locationNow.getTime() - locationPre.getTime();
		time = time/1000;
		speed = time/distance;
		float lat = (float) locationNow.getLatitude();
		float lon = (float) locationNow.getLongitude();

		String sql = String.format
				("insert into punti (npunto,distance,time,speed,lat,lon,names) VALUES (%s , %s , %s , %s , %s , %s , %s );",
						number, distance, time,speed, lat, lon, runName);

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

}
