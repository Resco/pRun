package com.example.projectrunning;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private Button run;
	private Button oldrun;
	private Button rerun;
	private TextView conta, conta2;
	private int RUN_CODE = 1;
	public ArrayList<String> places = new ArrayList<String>();
	private DBAdapter adapter;
	private String comando;
	private Cursor cursor;
	private Context contesto;




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//crea views e buttons
		conta = (TextView) findViewById(R.id.textView5);
		conta2 = (TextView) findViewById(R.id.textView6);
		run = (Button) findViewById(R.id.button2);
		rerun = (Button) findViewById(R.id.button3);
		oldrun = (Button) findViewById(R.id.button4);

		//setta onclick dei bottoni
		run.setOnClickListener(this);
		rerun.setOnClickListener(this);
		oldrun.setOnClickListener(this);
		
		//adapter
		adapter = new DBAdapter(this);
		adapter.open();
		
		//salva il context
		contesto = this;

		//TODO cancellare non più utile
		cursor = adapter.getAllEntriesPunti();
		conta.setText(cursor.getCount() + "");
		cursor = adapter.getAllEntriesCorse();
		conta2.setText(cursor.getCount() + "");


	}

	protected void onResume() {	
		
		super.onResume();

	}

	protected void onPause() {
		
		super.onPause();
	}
	
	public void onClick(View v) {
		switch (v.getId()){
		
		//bottone RUN
		case R.id.button2:
			Intent intentRun = new Intent(this, RunActivity.class); // Explicit intent creation
			startActivityForResult(intentRun, RUN_CODE); // Start as sub-activity for result
			break;
			
		//bottone RERUN
		case R.id.button3:

			break;
			
		//bottone SEE OLD RUN
		case R.id.button4:
			//TODO asynctask?
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

	protected void onActivityResult(
			int requestCode, 
			int resultCode,
			Intent pData) {
		if ( requestCode == RUN_CODE ) //se rientriamo dall'activiy RUN
		{
			if (resultCode == Activity.RESULT_OK ) 
			{
				places = pData.getStringArrayListExtra("locations");
				conta.setText(places.size() + "");
				//TODO asynctask?
				for (int i=0; i<places.size(); i++){	//ciclo che esegue e inserisce in databse
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
