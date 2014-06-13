package com.example.projectrunning;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.MediaStore;
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
	private TextView conta, conta2;
	private DBAdapter dbAdapter;
	private DBAdapter adapter;
	private String comando;
	private Cursor cursor;
	private Context contesto;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		latitude = (TextView) findViewById(R.id.textView2);
		longitude = (TextView) findViewById(R.id.textView4);
		conta = (TextView) findViewById(R.id.textView5);
		conta2 = (TextView) findViewById(R.id.textView6);
		counter = (TextView) findViewById(R.id.placesCounter);
		map = (Button) findViewById(R.id.button1);
		run = (Button) findViewById(R.id.button2);

		map.setOnClickListener(this);
		run.setOnClickListener(this);

		adapter = new DBAdapter(this);
		adapter.open();

		contesto = this;

		
		cursor = adapter.getAllEntriesPunti();
		conta.setText(cursor.getCount() + "");
		cursor = adapter.getAllEntriesCorse();
		conta2.setText(cursor.getCount() + "");


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
			            // TODO Auto-generated method stub
			        	
			        	for (int i = 0; i<MenuItems.length; i++){
			        		if (i==pos){
			        			//ciò che voglio fare quando clicco un elemento
			        			Toast toast = Toast.makeText(getApplicationContext(), MenuItems[i] + pos, Toast.LENGTH_SHORT);
			        			toast.show();
			        			Intent intent = new Intent(contesto, RunInfoActivity.class);
			        			intent.putExtra("id", MenuItems[i]);
								startActivity(intent);
			        		}
			        	}
			        	
//			        	
			            dialog1.cancel();
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

}
