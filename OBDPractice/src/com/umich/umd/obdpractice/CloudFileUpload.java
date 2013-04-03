package com.umich.umd.obdpractice;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class CloudFileUpload extends Activity {

	// Name of file where filenames of downloaded files are stored
	private final static String DOWNLOADED_LOG_FILES = "downloaded_logs";
	// Debug tag for identifying from which activity debug message
	// originated
	private final static String DEBUG_TAG = "CloudUpload";
	// Key for specifying which arraylist in bundle is the file list
	private final static String LIST_KEY = "filesList";

	// points to file title bar in view
	private TextView fileTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cloud_file_upload);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Build SelectFiles fragment for selecting files
		SelectFilesDialogFragment selectFiles =  new SelectFilesDialogFragment();
		// bundle to send to select file fragment storing file arraylist
		Bundle listBundle = new Bundle();
		// add arrayList to bundle
		listBundle.putStringArrayList(LIST_KEY, new ArrayList<String>(getLogFileList()));
		// set bundle to dialog fragment argument
		selectFiles.setArguments(listBundle);
		// show dialog fragment
		selectFiles.show(getFragmentManager(), "dialog");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_cloud_file_upload, menu);
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
	
	private HashSet<String> getLogFileList() {
		
		// BufferedReader for reading from logfile list file
		BufferedReader logListReader = null;
		// Last line read from BufferedReader, should be one filename
		String readLine;
		// HashSet to store filenames
		HashSet<String> logFileNames = new HashSet<String>();		
		
		try {
			// Create input stream from DOWNLOADED_LOG_FILES file
			logListReader = new BufferedReader( new InputStreamReader(
					openFileInput(DOWNLOADED_LOG_FILES)));
			
			while((readLine = logListReader.readLine()) != null)
			{
				Log.d(DEBUG_TAG, "Added FileName " + readLine);
				// add each filename to HashSet, duplicates are discarded
				logFileNames.add(readLine);
			}
			
		} catch(FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			
			try {
				logListReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return logFileNames;
		
	}

}
