package com.umich.umd.obdpractice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.View;

public class GetHTTPTask extends AsyncTask<String,Void,String>{

	private static final String GRYPH_IP="http://192.168.0.112/";
	private static final String LOG_FILE_SCRIPT="/sysadmin/playback_action.php";
	private static final String LIST_LOGS_PARAMS="?verb=list&uploaddir=/data/&extension=.log";
	private final static String TAG_JSON_OUTPUT = "json_string";
	
	private final static String USERNAME = "sysadmin";
	private final static String PASSWORD = "dggryphon";
	
	
	@Override
	protected String doInBackground(String... urls) {
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
			} catch (MalformedURLException ex) {
				ex.printStackTrace();
			}
			return stream;
		}
	
/*	@Override
	protected void onPostExecute(String output)
	{
		
		Intent filesListIntent = new Intent(this, LogFilesList.class);
		filesListIntent.putExtra(TAG_JSON_OUTPUT, output);
		startActivity(filesListIntent);

		readText.setText(output);
	}*/
	
/*	public void connectToGryphon(View view) {
		
		//URL listURL = new URL(GRYPH_IP+LOG_FILE_SCRIPT+LIST_LOGS_PARAMS);
		String listLogsURLString = GRYPH_IP+LOG_FILE_SCRIPT+LIST_LOGS_PARAMS;
		GetXMLTask task = new GetXMLTask();
		task.execute(new String [] {listLogsURLString});
		
	}*/
	
	

}
