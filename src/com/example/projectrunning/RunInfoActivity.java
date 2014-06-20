package com.example.projectrunning;

import java.util.ArrayList;

import com.example.projectrunning.RunActivity.CheckpointBuilder;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class RunInfoActivity extends Activity implements OnClickListener {

	private DBAdapter adapter;
	private TextView distance;
	private TextView title;
	private TextView time;
	private TextView calendar;
	private TextView comment;
	private ArrayList<LatLng> coords = new ArrayList<LatLng>();
	private Button mapButton;
	private Button delButton;
	private int MAP_CODE = 1;
	private String id;
	private Cursor cursor;
	private Void params;
	private listBuilder builder;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_run_info);

		//prende l'id e lo mette in versione con apici
		id = getIntent().getStringExtra("id");
		id = "'" + id + "'";

		//setto le varie view e button
		title = (TextView) findViewById(R.id.textView1);
		distance = (TextView) findViewById(R.id.textView3);
		time = (TextView) findViewById(R.id.textView5);
		calendar = (TextView) findViewById(R.id.textView6);
		comment = (TextView) findViewById(R.id.textView8);
		mapButton = (Button) findViewById(R.id.button1);
		delButton = (Button) findViewById(R.id.button2);

		mapButton.setEnabled(false);
		delButton.setEnabled(false);

		//setto gli onClickListener
		mapButton.setOnClickListener(this);
		delButton.setOnClickListener(this);

		//creo e apro l'adapter
		adapter = new DBAdapter(this);
		adapter.open();

		//cursore con la riga relativa al percorso scelto
		cursor = adapter.getIdRowFromCorse(id);

		//visualizzo le varie informazioni
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

		//popolo l'arraylist che passerò alla activity della mappa

		builder = new listBuilder();
		builder.execute(params);



	}


	@Override
	public void onClick(View v) {
		switch (v.getId()){
		//bottone MAP
		case R.id.button1:
			Intent intent = new Intent(this, MapActivity.class); // Explicit intent creation
			intent.putParcelableArrayListExtra("latlngs", coords);
			startActivityForResult(intent, MAP_CODE); // Start as sub-activity for result
			break;
			//bottone DELETE
		case R.id.button2:
			String sql = "DELETE FROM Corse WHERE names=" + id + ";";
			adapter.execute(sql);
			sql = "DELETE FROM Punti WHERE names=" + id + ";";
			adapter.execute(sql);
			finish();
		}
	}

	class listBuilder extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			cursor = adapter.getIdRowFromPunti(id);
			if (cursor.moveToFirst()){
				do{
					float lat = cursor.getFloat(cursor.getColumnIndex(DBContract.Punti.COLUMN_NAME_LAT));
					float lon = cursor.getFloat(cursor.getColumnIndex(DBContract.Punti.COLUMN_NAME_LON));
					LatLng mark = new LatLng(lat, lon);
					coords.add(mark);
				}while(cursor.moveToNext());
			}
			return null;
		}

		protected void onPostExecute(Void result){
			mapButton.setEnabled(true);
			delButton.setEnabled(true);
		}

	}
}
