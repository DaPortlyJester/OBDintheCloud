package com.umich.umd.obdpractice;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

import android.os.Bundle;
import android.app.Activity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class ViewStoredLogsActivity extends Activity {

	private final static String DOWNLOADED_LOG_FILES = "downloaded_logs";
	private final static String DEBUG_TAG = "ViewLogFiles";
	private final static String LIST_KEY = "filesList";
	
	private TextView fileTitle;
	private TextView fileContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_stored_logs);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		findViewsById();
		
		SelectFilesDialogFragment selectFiles =  new SelectFilesDialogFragment();
		Bundle listBundle = new Bundle();
		listBundle.putStringArrayList(LIST_KEY, new ArrayList<String>(getLogFileList()));
		selectFiles.setArguments(listBundle);
		selectFiles.show(getFragmentManager(), "dialog");
		
	}
	
	private void findViewsById() {
		fileTitle = (TextView) findViewById(R.id.file_name);
		fileContent = (TextView) findViewById(R.id.logfile_contents);
		fileContent.setMovementMethod(new ScrollingMovementMethod());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_view_stored_logs, menu);
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
		
		BufferedReader logListReader = null;
		String readLine;
		HashSet<String> logFileNames = new HashSet<String>();
		
		
		try {
			logListReader = new BufferedReader( new InputStreamReader(
					openFileInput(DOWNLOADED_LOG_FILES)));
			
			while((readLine = logListReader.readLine()) != null)
			{
				Log.d(DEBUG_TAG, "Added FileName " + readLine);
				logFileNames.add(readLine);
			}
			
		} catch(FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
	
	public void writeToScreen(String fileName) {
		int numLines = 0;
		fileTitle.setText(fileName);
		fileContent.setText("");
		
		BufferedReader fileReader = null;
		String rdLine;
		
		try {
			fileReader = new BufferedReader( new InputStreamReader(
					openFileInput(fileName)));
			
			while((rdLine = fileReader.readLine()) != null && numLines < 100)
			{
				
				fileContent.append(rdLine + "\n");
				numLines++;
			}
			
		} catch(FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			
			try {
				fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public void selectFile(View vw){
		SelectFilesDialogFragment selectFiles =  new SelectFilesDialogFragment();
		Bundle listBundle = new Bundle();
		listBundle.putStringArrayList(LIST_KEY, new ArrayList<String>(getLogFileList()));
		selectFiles.setArguments(listBundle);
		selectFiles.show(getFragmentManager(), "dialog");
	}
	
	public void endView(View vw){
		finish();
	}
}
