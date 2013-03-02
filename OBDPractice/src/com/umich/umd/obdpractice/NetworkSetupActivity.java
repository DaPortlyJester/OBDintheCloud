package com.umich.umd.obdpractice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.net.URL;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class NetworkSetupActivity extends Activity{
	
	private static final String DEBUG_TAG="NetworkConnect";
	private static final String GRYPH_IP="http://192.168.0.112/";
	private static final String LOG_FILE_SCRIPT="/sysadmin/playback_action.php";
	private static final String LIST_LOGS_PARAMS="?verb=list&uploaddir=/data/&extension=.log";
	
	private final static String USERNAME = "sysadmin";
	private final static String PASSWORD = "dggryphon";
	
	private EditText urlText;
	private TextView readText;
	private Button listButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_network_setup);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		findViewsById();
	}
	
	private void findViewsById() {
		urlText = (EditText) findViewById(R.id.networkIP);
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
				BufferedReader buffer = new BufferedReader ( new InputStreamReader(stream));
				String s = "";
				while((s = buffer.readLine()) != null)
					output.append(s);
			}
			catch(IOException e1)
			{
				e1.printStackTrace();
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
			
			// prepare authorization string
	        userPassword = String.format("%s:%s", USERNAME, PASSWORD);
	        int flags = Base64.NO_WRAP | Base64.URL_SAFE;
	        encoding = Base64.encodeToString(userPassword.getBytes(), flags);
	        
	        // Decided to use Java based Base64 encoder instead of sun encoder
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
			} catch (MalformedURLException ex) {
				ex.printStackTrace();
			}
			return stream;
		}
		
		@Override
		protected void onPostExecute(String output)
		{
			readText.setText(output);
		}
	}
	
	public void connectToGryphon(View view) {
		
		//URL listURL = new URL(GRYPH_IP+LOG_FILE_SCRIPT+LIST_LOGS_PARAMS);
		String listLogsURLString = GRYPH_IP+LOG_FILE_SCRIPT+LIST_LOGS_PARAMS;
		GetXMLTask task = new GetXMLTask();
		task.execute(new String [] {listLogsURLString});
		
	}
}
