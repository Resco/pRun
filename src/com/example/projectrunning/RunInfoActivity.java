package com.example.projectrunning;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RunInfoActivity extends Activity implements OnClickListener {

	private DBAdapter adapter;
	private TextView distance;
	private TextView title;
	private TextView time;
	private TextView calendar;
	private TextView comment;
	private ArrayList<LatLng> coords = new ArrayList<LatLng>();
	private Button mapButton;
	private int MAP_CODE = 1;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_run_info);
		String id = getIntent().getStringExtra("id");
		id = "'" + id + "'";
		
		title = (TextView) findViewById(R.id.textView1);
		distance = (TextView) findViewById(R.id.textView3);
		time = (TextView) findViewById(R.id.textView5);
		calendar = (TextView) findViewById(R.id.textView6);
		comment = (TextView) findViewById(R.id.textView8);
		mapButton = (Button) findViewById(R.id.button1);

		mapButton.setOnClickListener(this);
		
		adapter = new DBAdapter(this);
		adapter.open();
		
		Cursor cursor = adapter.getIdRowFromCorse(id);
		
		
		Toast toast = Toast.makeText(getApplicationContext(), cursor.getColumnCount() +"", Toast.LENGTH_SHORT);
		toast.show();
		
		title.setText(id);
		cursor.moveToFirst();
		
		String comm = cursor.getString(cursor.getColumnIndex(DBContract.Corse.COLUMN_NAME_COMMENT));
		comment.setText(comm);	
		
		Float dist = cursor.getFloat(cursor.getColumnIndex(DBContract.Corse.COLUMN_NAME_DISTANCE));
		distance.setText(dist + "");
		
		Float tim = cursor.getFloat(cursor.getColumnIndex(DBContract.Corse.COLUMN_NAME_TIME));
		time.setText(tim + "");
		
		String cal = cursor.getString(cursor.getColumnIndex(DBContract.Corse.COLUMN_NAME_DATA));
		calendar.setText(cal);
		
		
		cursor = adapter.getIdRowFromPunti(id);
		if (cursor.moveToFirst()){
			do{
				float lat = cursor.getFloat(cursor.getColumnIndex(DBContract.Punti.COLUMN_NAME_LAT));
				float lon = cursor.getFloat(cursor.getColumnIndex(DBContract.Punti.COLUMN_NAME_LON));
				LatLng mark = new LatLng(lat, lon);
				coords.add(mark);
			}while(cursor.moveToNext());
		}
		
		


	}


	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this, MapActivity.class); // Explicit intent creation
		intent.putParcelableArrayListExtra("latlngs", coords);
		startActivityForResult(intent, MAP_CODE); // Start as sub-activity for result	
	}
}
