package com.example.projectrunning;

import java.util.ArrayList;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;

public class MapActivity extends Activity {

	public double lon;
	public ArrayList<Location> placesMap = new ArrayList<Location>();
	private ArrayList<Parcelable> coords;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

		//ArrayList con le coordinate
		coords = getIntent().getParcelableArrayListExtra("latlngs");

		//Ciclo per disegnare il traccaito
		LatLng temp1 = (LatLng) coords.get(0);
		for(int i=1;i<coords.size();i++){
			LatLng temp2 = (LatLng) coords.get(i);
			//map.addMarker(new MarkerOptions().title("Sei qui").position(temp));	//aggiunge un marker.
			map.addPolyline(new PolylineOptions().add(temp1, temp2).color(Color.RED).width(5));
			temp1 = temp2;
		}

		//Determinazione del centro dello zoom
		int resto = coords.size()%2;
		LatLng zoomer;

		if (resto == 0)
			zoomer = (LatLng) coords.get(coords.size()/2);
		else
			zoomer = (LatLng) coords.get((coords.size()+1)/2);

		map.moveCamera(CameraUpdateFactory.newLatLngZoom(zoomer, 15));
	}

}
