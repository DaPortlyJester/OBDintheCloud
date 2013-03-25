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

	// Name of file where filenames of downloaded files are stored
	private final static String DOWNLOADED_LOG_FILES = "downloaded_logs";
	// Debug tag for identifying from which activity debug message 
	// originated
	private final static String DEBUG_TAG = "ViewLogFiles";
	// Key for specifying which arraylist in bundle is the file list
	private final static String LIST_KEY = "filesList";
	
	// points to file title bar in view
	private TextView fileTitle;
	// points to file content section in view
	private TextView fileContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_stored_logs);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Assign view IDs to variables fileTitle and fileContent
		findViewsById();
		
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
	
	/**
	 * Assign View fields to class accessible variables
	 */
	private void findViewsById() {
		// assign file_name TextView to fileTitle
		fileTitle = (TextView) findViewById(R.id.file_name);
		// assign file contents TextView to fileContent
		fileContent = (TextView) findViewById(R.id.logfile_contents);
		// allow vertical scrolling on file contents from xml layout definition
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

	/**
	 * Build list of log files from stored list of log files
	 * in file DOWNLOADED_LOG_FILES
	 * 
	 * List of log files maintained in HashSet to simply remove
	 * any duplicate file names
	 * 
	 * Consider creating a class to do all file maintenance, so
	 * duplicates of the same file name will not be saved to 
	 * log file list file.
	 * 
	 * @return a HashSet of file names that
	 */
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
	
	/**
	 * Write the data contained in the selected file with
	 * name file name. Currently writes a maximum of 100 lines
	 * to the screen. Maybe find a way to add more data as the
	 * screen reaches the end of the scrollable area
	 * 
	 * @param fileName The name of the file whose contents
	 * 			are going to be displayed on screen (in the TextView)
	 */
	public void writeToScreen(String fileName) {
		int numLines = 0;
		fileTitle.setText(fileName);
		fileContent.setText("");
		
		BufferedReader fileReader = null;
		String rdLine;
		
		try {
			// Open a stream to the file with fileName
			fileReader = new BufferedReader( new InputStreamReader(
					openFileInput(fileName)));
			
			/*
			 * While there is data to be read in file and the number of lines
			 * read from the file is less than 100, continue reading 
			 */
			while((rdLine = fileReader.readLine()) != null && numLines < 100)
			{
				// append last line read to TextView
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
	
	/**
	 * If the user clicks the select another file button, build a new
	 * select file dialog fragment, with the list of log files for the user to
	 * choose from
	 * 
	 * @param vw The view where the click originated from
	 */
	public void selectFile(View vw){
		SelectFilesDialogFragment selectFiles =  new SelectFilesDialogFragment();
		Bundle listBundle = new Bundle();
		listBundle.putStringArrayList(LIST_KEY, new ArrayList<String>(getLogFileList()));
		selectFiles.setArguments(listBundle);
		selectFiles.show(getFragmentManager(), "dialog");
	}
	
	/**
	 * 
	 * Allow the user to exit the current activity without pressing the back button
	 * 
	 * @param vw The view where the click action originated from
	 */
	public void endView(View vw){
		finish();
	}
}
