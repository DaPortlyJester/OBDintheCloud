package com.umich.umd.obdpractice;


import java.util.ArrayList;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class LogFilesList extends ListActivity {
	
	private final static String TAG_JSON_OUTPUT = "json_string";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_log_files_list);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Intent i = getIntent();
		String jsonOutput = i.getStringExtra(TAG_JSON_OUTPUT);
		
		JSONParser jsonParser = new JSONParser();
		
		ArrayList<String> filesList = jsonParser.jsonToList(jsonOutput);
		
		this.setListAdapter(new ArrayAdapter<String>(getApplicationContext(),R.layout.list_item,filesList));
		
		ListView lv = getListView();
		
		lv.setOnItemClickListener(new OnItemClickListener() {
	          public void onItemClick(AdapterView<?> parent, View view,
	              int position, long id) {
	 
	              // selected item
	              String baseFileName = ((TextView) view).getText().toString();
	 
	              // Return to parent Activity
	              Intent fileIntent = new Intent();
	              fileIntent.putExtra("file", baseFileName);
	              setResult(RESULT_OK, fileIntent);
	              finish();
	 
	          }
	        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_log_files_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
