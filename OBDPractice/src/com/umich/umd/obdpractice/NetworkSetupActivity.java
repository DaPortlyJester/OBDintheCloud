package com.umich.umd.obdpractice;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.EditText;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class NetworkSetupActivity extends Activity{
	
	private static final String DEBUG_TAG = "NetworkConnect";
	private static final String SCHEME_TYPE = "http";
	private static final String GRYPH_IP = "http://192.168.0.112";
	private static final String LIST_LOG_FILE_SCRIPT = "/sysadmin/playback_action.php";
	private static final String DOWNLOAD_LOG_FILE_SCRIPT = "/sysadmin/log_action.php";
	private static final String LIST_LOGS_PARAMS = "?verb=list&uploaddir=/data/&extension=.log";
	private static final String DOWNLOAD_LOG_PARAMS = "?uploaddir=/data/&extension=.log&type=ascii&name=";
	private static final String DOWNLOAD_VERB = "&verb=Download";
	private final static String TAG_JSON_OUTPUT = "json_string";
	
	private final static String DOWNLOADED_LOG_FILES = "downloaded_logs";
	
	private final static String USERNAME = "sysadmin";
	private final static String PASSWORD = "dggryphon";
	
	private final static int PICK_FILE_REQUEST = 0;
	private final static int DOWNLOAD_FILE_REQUEST = 1;
	private static int CURRENT_REQUEST = 0;
	
	private boolean log_file_picked = false;
	private static String curr_log_file_base_name = null;
	private static String last_log_file_base_name = null;
	
	private boolean accessAuthenticated = false;
	
	private TextView readText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_network_setup);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		findViewsById();
	}
	
	private void findViewsById() {
		readText = (TextView) findViewById(R.id.fileText);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_network_setup, menu);
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
	 * Android tutorial based function, ignore
	 * @param view
	 */
	/*public void connectToGryphon(View view) {
		
		String stringURL = urlText.getText().toString();
		
		ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		
		if(networkInfo != null && networkInfo.isConnected())
		{
			//new DownloadWebpageText().execute(stringURL);
			
		} else 
		{
			textView.setText("No network connection available.");
		}
	}*/
	
	/**
	 * 
	 * @author AbsElite
	 *
	 */
	private class GetXMLTask extends AsyncTask<String,Void,String>
	{
		protected String doInBackground(String... urls)
		{
			String output = null;
			for(String url: urls) {
				output = getOutputFromUrl(url);
			}
			return output;
		}
		
		protected String getOutputFromUrl(String url)
		{
			Log.d(DEBUG_TAG,"Attempting to instantiate StringBuffer");
			StringBuffer output = new StringBuffer("");
			Log.d(DEBUG_TAG,"StringBufferIn");
			try
			{
				Log.d(DEBUG_TAG,"Trying Input Stream");
				InputStream stream = getHttpConnection(url);
				Log.d(DEBUG_TAG,"InputStream successful");
				int count = 0;
				
				Log.d(DEBUG_TAG,"Trying stream availability check.");
				int streamState = stream.available();
				Log.d(DEBUG_TAG,"Stream check successful.");
				
				while(stream.available()!=0 && count < 20){
					Log.d(DEBUG_TAG,"Stream not available. Waiting");
					this.wait(100);
					count+=1;
				};
				if(count > 19)
				{
					Log.e(DEBUG_TAG,"HttpConnection stream didn't become available");
					finish();
				}
				Log.d(DEBUG_TAG,"Preparing to initiate BufferedReader");
				BufferedReader buffer = new BufferedReader ( new InputStreamReader(stream));
				Log.d(DEBUG_TAG,"BufferedReader instantiated");
				String s = "";
				
				while((s = buffer.readLine()) != null)
					output.append(s);
			}
			catch(IOException e1)
			{
				e1.printStackTrace();
			}
			catch(NullPointerException ex)
			{
				Log.e(DEBUG_TAG, "NullPointer found in BufferedReader", ex);
				ex.printStackTrace();
			} catch (InterruptedException e) {
				Log.d(DEBUG_TAG, "Wait in getOutputFromURL Interrupted", e);
				e.printStackTrace();
			}
			return output.toString();
		}
		
		private InputStream getHttpConnection(String urlString)
			throws IOException
		{
			InputStream stream = null;
			HttpURLConnection httpConnection;
			URL url;
			
			String userPassword, encoding;
			
			// prepare authorization string using android.util.Base64
	        userPassword = String.format("%s:%s", USERNAME, PASSWORD);
	        int flags = Base64.NO_WRAP | Base64.URL_SAFE;
	        encoding = Base64.encodeToString(userPassword.getBytes(), flags);
	        
	        // Used android based Base64 encoder instead of sun encoder
	        // Expected to perform better than Sun version
	        //encoding = new sun.misc.BASE64Encoder().encode(userPassword.getBytes());
			
			try {
				
				// Use built in functions of url to convert url string to html encoded version
				url = new URL(urlString);
				URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
				url = uri.toURL();
				
				Log.d(DEBUG_TAG,"The current URL is: " + url.toString());
				// Open HttpURLConnection
				httpConnection = (HttpURLConnection) url.openConnection();
				// Append authorization string to HTTP request header
				if(!accessAuthenticated) {
					httpConnection.setRequestProperty("Authorization", "Basic " + encoding);
					//accessAuthenticated = true;
				}
				
				/*
				 * From open Tutorials example, using DGTech solution
				httpConnection.setRequestMethod("GET");
				httpConnection.connect();
				*/
				
				if(httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
				{
					Log.d(DEBUG_TAG,"HttpURLConnection: HTTP_OK");
					stream = httpConnection.getInputStream();
					Log.d(DEBUG_TAG,"Stream for httpConnection set");
				}
			} catch (MalformedURLException ex) {
				Log.e(DEBUG_TAG, "Malformed URL", ex);
				ex.printStackTrace();
			} catch (URISyntaxException e) {
				Log.e(DEBUG_TAG, "URI to URL convetsion failed", e);
				e.printStackTrace();
			}
			
			return stream;
		}
		
		@Override
		protected void onPostExecute(String output)
		{
			if(CURRENT_REQUEST == PICK_FILE_REQUEST) {
			
				Intent filesListIntent = new Intent(getApplicationContext(), LogFilesList.class);
				filesListIntent.putExtra(TAG_JSON_OUTPUT, output);
				startActivityForResult(filesListIntent,PICK_FILE_REQUEST);
			} else {
				try {
					writeToFile(output);
					FileOutputStream dlLogFile = openFileOutput(curr_log_file_base_name + ".txt",Context.MODE_PRIVATE);
					dlLogFile.write(output.getBytes());
					dlLogFile.close();
					readText.setText("File download successful: "+ curr_log_file_base_name);
					writeToFile(curr_log_file_base_name,DOWNLOADED_LOG_FILES);
					last_log_file_base_name = curr_log_file_base_name;
					curr_log_file_base_name = null;
				}catch (Exception e) {
					Log.e(DEBUG_TAG, "Error while saving log to file", e);
				}
				
			}
		}
	}
	
	public void pickLogFile(View view) {
		
		CURRENT_REQUEST = PICK_FILE_REQUEST;
		String listLogsURLString = GRYPH_IP+LIST_LOG_FILE_SCRIPT+LIST_LOGS_PARAMS;
		Log.d(DEBUG_TAG, "The list log URL is:" + listLogsURLString);
		GetXMLTask task = new GetXMLTask();
		task.execute(new String [] {listLogsURLString});
	}
	
	public void downloadFile(View view) {
		
		if(log_file_picked)
		{
			CURRENT_REQUEST = DOWNLOAD_FILE_REQUEST;
			String downloadLogURLString = GRYPH_IP+DOWNLOAD_LOG_FILE_SCRIPT+DOWNLOAD_LOG_PARAMS+curr_log_file_base_name+DOWNLOAD_VERB;
			Log.d(DEBUG_TAG, "The download URL is:" + downloadLogURLString);
			readText.setText(downloadLogURLString);
			GetXMLTask task = new GetXMLTask();
			task.execute(new String [] {downloadLogURLString});
			log_file_picked = false;
		} else {
			readText.setText("No File Picked. Please Choose a Log File First");
		}
			
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
        if (requestCode == PICK_FILE_REQUEST) {
            if (resultCode == RESULT_OK) {
                // A file was picked, display it to the user
            	curr_log_file_base_name = data.getStringExtra("file");
                readText.setText(curr_log_file_base_name);
                log_file_picked = true;
            }
        }
    }
	
	private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(curr_log_file_base_name, Context.MODE_WORLD_READABLE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e(DEBUG_TAG, "File write failed: " + e.toString());
        }
	}
	
	private void writeToFile(String data, String filename) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(filename, Context.MODE_WORLD_READABLE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e(DEBUG_TAG, "File write failed: " + e.toString());
        }
	}
}
