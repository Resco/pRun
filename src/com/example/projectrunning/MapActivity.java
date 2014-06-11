package com.example.projectrunning;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MapActivity extends Activity {

	private final String TAG = this.getClass().getCanonicalName();
	private LocationListener myLocationListener;
	private Toast tvLatitude;
	private Toast tvLongitude;
	private Toast tvAltityde;
	private String provider = LocationManager.GPS_PROVIDER;
	private double lat;
	public double lon;
	public ArrayList<Location> placesMap = new ArrayList<Location>();
	private double prelat;
	private double prelon;
	private LatLng mntgnrpre;
	private DBAdapter adapter;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		// Get a handle to the Map Fragment
		GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

		//		placesMap = getIntent().getParcelableArrayListExtra("array");
		//		int counter = placesMap.size();
		//		Location prevLoc;
		//		Location lastLoc = placesMap.get(0);
		//		lat = lastLoc.getLatitude();
		//		lon = lastLoc.getLongitude();
		//		LatLng mntgnr = new LatLng(lat, lon);
		//		map.moveCamera(CameraUpdateFactory.newLatLngZoom(mntgnr, 20));		//posizione la camera
		//
		//		for(int i=1; i<counter; i++ ){
		//		lastLoc = placesMap.get(i);
		//		prevLoc = placesMap.get(i-1);
		//		lat = lastLoc.getLatitude();
		//		lon = lastLoc.getLongitude();
		//		mntgnr = new LatLng(lat, lon);								//setta una coppia LAT-LONG
		//		prelat = prevLoc.getLatitude();
		//		prelon = prevLoc.getLongitude();
		//		mntgnrpre = new LatLng(prelat, prelon);
		//		map.addMarker(new MarkerOptions().title("Sei qui").position(mntgnr));	//aggiunge un marker.
		//		map.addPolyline(new PolylineOptions().add(mntgnr, mntgnrpre).color(Color.RED).width(5));
		//		}

		
		//GIUSTO
		
		adapter = new DBAdapter(this);
		adapter.open();

		Cursor cursor = adapter.getAllEntries();
		if (cursor.moveToFirst()){
			do{
				float lat = cursor.getFloat(cursor.getColumnIndex("lat"));
				float lon = cursor.getFloat(cursor.getColumnIndex("lon"));
				LatLng markCo = new LatLng(lat, lon);
				map.addMarker(new MarkerOptions().title("Sei qui").position(markCo));	//aggiunge un marker.
			}while(cursor.moveToNext());
		}
		cursor.close();




	}



	//	private void updateLocation(Location location){
	//		if (location == null)
	//			return;
	//		double latitude= location.getLatitude();
	//		double longitude = location.getLongitude();
	//		double altitude = location.getAltitude();
	//		float accurancy = location.getAccuracy();
	//		
	//		
	//		tvLatitude.setText("Latitude: " + latitude);
	//		tvLongitude.setText("Longitude: " + longitude);
	//
	//		
	//		Geocoder gc = new Geocoder(MapActivity.this,Locale.getDefault());
	//		List<Address> addresses = null;
	//		try {
	//			addresses = gc.getFromLocation(latitude, longitude, 10);
	//		} catch (IOException e) {
	//			Log.e(TAG, "IO Exception", e);
	//		}
	//	}

}
