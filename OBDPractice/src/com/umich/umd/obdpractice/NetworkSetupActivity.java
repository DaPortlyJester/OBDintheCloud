package com.umich.umd.obdpractice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.EditText;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class NetworkSetupActivity extends Activity{
	
//	private static final String DEBUG_TAG="NetworkConnect";
	private static final String GRYPH_IP="http://192.168.0.112/";
	private static final String LIST_LOG_FILE_SCRIPT="/sysadmin/playback_action.php";
	private static final String DOWNLOAD_LOG_FILE_SCRIPT="/sysadmin/log_action.php";
	private static final String LIST_LOGS_PARAMS="?verb=list&uploaddir=/data/&extension=.log";
	private static final String DOWNLOAD_LOG_PARAMS="?uploaddir=/data/&extension=.log&type=ascii&verb=Download&name=";
	private final static String TAG_JSON_OUTPUT = "json_string";
	
	private final static String USERNAME = "sysadmin";
	private final static String PASSWORD = "dggryphon";
	
	private final static int PICK_FILE_REQUEST = 0;
	private final static int DOWNLOAD_FILE_REQUEST = 1;
	private static int CURRENT_REQUEST = 0;
	
	private boolean log_file_picked = false;
	private static String curr_log_file_base_name = null;
	private static String last_log_file_base_name = null;
	
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
			StringBuffer output = new StringBuffer("");
			try
			{
				InputStream stream = getHttpConnection(url);
				int count = 0;
				while(stream.available()!=0 && count < 20){ 
					this.wait(100);
					count+=1;
				};
				if(count > 19)
				{
					//readText.setText("stream never produced bytes");
					finish();
				}
				BufferedReader buffer = new BufferedReader ( new InputStreamReader(stream));
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
				readText.setText("NullPointerException");
				ex.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
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
				url = new URL(urlString);
				// Open HttpURLConnection
				httpConnection = (HttpURLConnection) url.openConnection();
				// Append authorization string to HTTP request header
				httpConnection.setRequestProperty("Authorization", "Basic " + encoding);
				
				/*
				 * From open Tutorials example, using DGTech solution
				httpConnection.setRequestMethod("GET");
				httpConnection.connect();
				*/
				if(httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
				{
					stream = httpConnection.getInputStream();
				}
				//httpConnection.disconnect();
			} catch (MalformedURLException ex) {
				ex.printStackTrace();
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
	
				// readText.setText(output);
			} else {
				readText.setText(output);
			}
		}
	}
	
	public void pickLogFile(View view) {
		
		CURRENT_REQUEST = PICK_FILE_REQUEST;
		String listLogsURLString = GRYPH_IP+LIST_LOG_FILE_SCRIPT+LIST_LOGS_PARAMS;
		GetXMLTask task = new GetXMLTask();
		task.execute(new String [] {listLogsURLString});
	}
	
	public void downloadFile(View view) {
		
		if(log_file_picked)
		{
			CURRENT_REQUEST = DOWNLOAD_FILE_REQUEST;
			String downloadLogURLString = GRYPH_IP+DOWNLOAD_LOG_FILE_SCRIPT+DOWNLOAD_LOG_PARAMS+curr_log_file_base_name;
			readText.setText(downloadLogURLString);
			GetXMLTask task = new GetXMLTask();
			task.execute(new String [] {downloadLogURLString});
			last_log_file_base_name = curr_log_file_base_name;
			curr_log_file_base_name = null;
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
}
