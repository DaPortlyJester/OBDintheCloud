package com.umich.umd.obdpractice;

import java.io.BufferedReader;
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
import java.util.ArrayList;

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

public class NetworkSetupActivity extends Activity {

	/**************************** Network Setup Activity Variables *************************/
	private static final String DEBUG_TAG = "NetworkConnect";
	// private static final String SCHEME_TYPE = "http";
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
	/************************* End of Network Setup Activity Variables ********************/

	/************************* File Parsing Variables **************************************/

	BufferedReader reader;
	// InputStream input;
	String line;
	int lineInt;
	String date = null;
	int length;
	int vehSpd_int;
	float vehSpd_flt;
	int eng_cool_temp_int;
	float eng_cool_temp_flt;
	int fuel_flow_int;
	float fuel_flow_flt;
	String vehSpd_str;
	String eng_cool_temp_str;
	String fuel_flow_str;

	ArrayList<Integer> CAN_VEH_SPD = new ArrayList<Integer>();
	ArrayList<Integer> CAN_ENG_COOL_TEMP = new ArrayList<Integer>();
	ArrayList<Float> CAN_FUEL_FLOW = new ArrayList<Float>();

	int i = 0;
	int j = 0;
	int k = 0;

	/************************** End of Log File Parsing Variables **************************/

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
	 * 
	 * @param view
	 */
	/*
	 * public void connectToGryphon(View view) {
	 * 
	 * String stringURL = urlText.getText().toString();
	 * 
	 * ConnectivityManager connMgr =
	 * (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
	 * 
	 * NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	 * 
	 * if(networkInfo != null && networkInfo.isConnected()) { //new
	 * DownloadWebpageText().execute(stringURL);
	 * 
	 * } else { textView.setText("No network connection available."); } }
	 */

	/**
	 * 
	 * @author AbsElite
	 * 
	 */
	private class GetXMLTask extends AsyncTask<String, Void, String> {
		
		/**
		 * 
		 */
		protected String doInBackground(String... urls) {
			String output = null;
			for (String url : urls) {
				output = getOutputFromUrl(url);
			}
			return output;
		}

		protected String getOutputFromUrl(String url) {
			Log.d(DEBUG_TAG, "Attempting to instantiate StringBuffer");
			StringBuffer output = new StringBuffer("");
			Log.d(DEBUG_TAG, "StringBufferIn");
			try {
				Log.d(DEBUG_TAG, "Trying Input Stream");
				InputStream stream = getHttpConnection(url);
				Log.d(DEBUG_TAG, "InputStream successful");
				int count = 0;

				Log.d(DEBUG_TAG, "Trying stream availability check.");
				
				while (stream.available() != 0 && count < 20) {
					Log.d(DEBUG_TAG, "Stream not available. Waiting");
					this.wait(100);
					count += 1;
				}
				;
				if (count > 19) {
					Log.e(DEBUG_TAG,
							"HttpConnection stream didn't become available");
					finish();
				}
				Log.d(DEBUG_TAG,"HttpStream available");
				Log.d(DEBUG_TAG, "Preparing to initiate BufferedReader");
				BufferedReader buffer = new BufferedReader(
						new InputStreamReader(stream));
				Log.d(DEBUG_TAG, "BufferedReader instantiated");
				String s = "";

				while ((s = buffer.readLine()) != null)
					output.append(s + "\n");
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (NullPointerException ex) {
				Log.e(DEBUG_TAG, "NullPointer found in BufferedReader", ex);
				ex.printStackTrace();
			} catch (InterruptedException e) {
				Log.d(DEBUG_TAG, "Wait in getOutputFromURL Interrupted", e);
				e.printStackTrace();
			}
			return output.toString();
		}

		private InputStream getHttpConnection(String urlString)
				throws IOException {
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
			// encoding = new
			// sun.misc.BASE64Encoder().encode(userPassword.getBytes());

			try {

				// Use built in functions of url to convert url string to html
				// encoded version
				url = new URL(urlString);
				URI uri = new URI(url.getProtocol(), url.getUserInfo(),
						url.getHost(), url.getPort(), url.getPath(),
						url.getQuery(), url.getRef());
				url = uri.toURL();

				Log.d(DEBUG_TAG, "The current URL is: " + url.toString());
				// Open HttpURLConnection
				httpConnection = (HttpURLConnection) url.openConnection();
				// Append authorization string to HTTP request header
				if (!accessAuthenticated) {
					httpConnection.setRequestProperty("Authorization", "Basic "
							+ encoding);
					// accessAuthenticated = true;
				}

				/*
				 * From open Tutorials example, using DGTech solution
				 * httpConnection.setRequestMethod("GET");
				 * httpConnection.connect();
				 */

				if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					Log.d(DEBUG_TAG, "HttpURLConnection: HTTP_OK");
					stream = httpConnection.getInputStream();
					Log.d(DEBUG_TAG, "Stream for httpConnection set");
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
		protected void onPostExecute(String output) {
			if (CURRENT_REQUEST == PICK_FILE_REQUEST) {

				Intent filesListIntent = new Intent(getApplicationContext(),
						LogFilesList.class);
				filesListIntent.putExtra(TAG_JSON_OUTPUT, output);
				startActivityForResult(filesListIntent, PICK_FILE_REQUEST);
			} else {
				try {
					readText.setText("");
					writeToFile(output);
					parseLogFile(output);
					FileOutputStream dlLogFile = openFileOutput(
							curr_log_file_base_name + ".txt",
							Context.MODE_PRIVATE);
					dlLogFile.write(output.getBytes());
					dlLogFile.close();
					// readText.setText("File download successful: "+
					// curr_log_file_base_name);
					writeToFile(curr_log_file_base_name, DOWNLOADED_LOG_FILES);
					last_log_file_base_name = curr_log_file_base_name;
					curr_log_file_base_name = null;
				} catch (Exception e) {
					Log.e(DEBUG_TAG, "Error while saving log to file", e);
				}

			}
		}
	}

	public void pickLogFile(View view) {

		CURRENT_REQUEST = PICK_FILE_REQUEST;
		String listLogsURLString = GRYPH_IP + LIST_LOG_FILE_SCRIPT
				+ LIST_LOGS_PARAMS;
		Log.d(DEBUG_TAG, "The list log URL is:" + listLogsURLString);
		GetXMLTask task = new GetXMLTask();
		task.execute(new String[] { listLogsURLString });
	}

	public void downloadFile(View view) {

		if (log_file_picked) {
			CURRENT_REQUEST = DOWNLOAD_FILE_REQUEST;
			String downloadLogURLString = GRYPH_IP + DOWNLOAD_LOG_FILE_SCRIPT
					+ DOWNLOAD_LOG_PARAMS + curr_log_file_base_name
					+ DOWNLOAD_VERB;
			Log.d(DEBUG_TAG, "The download URL is:" + downloadLogURLString);
			readText.setText(downloadLogURLString);
			GetXMLTask task = new GetXMLTask();
			task.execute(new String[] { downloadLogURLString });
			log_file_picked = false;
		} else {
			readText.setText("No File Picked. Please Choose a Log File First");
		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					openFileOutput(curr_log_file_base_name,
							Context.MODE_PRIVATE));
			outputStreamWriter.write(data);
			Log.d(DEBUG_TAG, "Output written to " + curr_log_file_base_name);
			outputStreamWriter.close();
		} catch (IOException e) {
			Log.e(DEBUG_TAG, "File write failed: " + e.toString());
		}
	}

	private void writeToFile(String data, String filename) {
		try {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					openFileOutput(filename, Context.MODE_PRIVATE));
			outputStreamWriter.write(data + "\n");
			outputStreamWriter.close();
		} catch (IOException e) {
			Log.e(DEBUG_TAG, "File write failed: " + e.toString());
		}
	}

	private void parseLogFile(String data) {

		FileOutputStream fos = null;
		InputStream fis = null;
		try {
			fos = openFileOutput("parsed" + curr_log_file_base_name, Context.MODE_PRIVATE);
			fis = openFileInput(curr_log_file_base_name);
			reader = new BufferedReader(new InputStreamReader(fis));

			while ((line = reader.readLine()) != null) {
				//Log.d(DEBUG_TAG, "The line read is " + lineInt);
				//line = ((Integer) lineInt).toString();
				//Log.d(DEBUG_TAG, "The line is " + line);
				if (line.contains("Trigger occurred at")) {
					date = line;
					date = date.replaceAll("/", "");
					//Log.d(DEBUG_TAG, "Date Found: " + date);
				} else if (line.length() == 0) {
					//Log.d(DEBUG_TAG, "No information");

				} else if (line.contains("Chan")) {
					String modLine = line.replaceAll("\\W", "");
					String[] columns = modLine.split("Rx");
					String CANidMsg = columns[1];
					//Log.d(DEBUG_TAG, "The CANidMsg is" + CANidMsg);

					if ((CANidMsg.substring(0, 4)).equals("0201")) {
						vehSpd_str = CANidMsg.substring(12, 16);
						/*Log.d(DEBUG_TAG, "Found VehSpd: " + vehSpd_str);*/
						vehSpd_int = Integer.parseInt(vehSpd_str, 16);
						vehSpd_int = (int) (((vehSpd_int * 0.01) - 100) * (0.62));

						CAN_VEH_SPD.add(vehSpd_int); // 4-19
						/*Log.d(DEBUG_TAG,
								"Value added to Array: "
										+ String.valueOf(vehSpd_int));*/

						fos.write((CAN_VEH_SPD.get(i).toString()).getBytes());

					} else if ((CANidMsg.substring(0, 4)).equals("0420")) {

						eng_cool_temp_str = CANidMsg.substring(4, 6);
						/*Log.d(DEBUG_TAG, "Enging Cool Temp Found"
								+ eng_cool_temp_str);*/
						eng_cool_temp_int = Integer.parseInt(eng_cool_temp_str,
								16);
						eng_cool_temp_int = (int) (eng_cool_temp_int - 40);

						CAN_ENG_COOL_TEMP.add(eng_cool_temp_int);

						fuel_flow_str = CANidMsg.substring(8, 10);
/*						Log.d(DEBUG_TAG, "Fuel Flow Found: " + fuel_flow_str);
*/						fuel_flow_int = Integer.parseInt(fuel_flow_str, 16);
						fuel_flow_flt = (float) (fuel_flow_int * 0.000020833);

						CAN_FUEL_FLOW.add(fuel_flow_flt);

						/*
						 * textV.append(CAN_FUEL_FLOW.get(i).toString());
						 * textV.append("\n"); i++;
						 */
					}
				}
			}
			fos.close();

			for (int a = 0; a < CAN_VEH_SPD.size(); a++) {
				vehSpd_flt += CAN_VEH_SPD.get(a);
			}
			vehSpd_flt = vehSpd_flt / CAN_VEH_SPD.size();
			vehSpd_flt = (float) Math.round(vehSpd_flt * 100) / 100;
			vehSpd_str = Float.toString(vehSpd_flt);
			readText.append("Vehicle Speed ");
			readText.append(vehSpd_str);
			readText.append("\n");

			for (int b = 0; b < CAN_ENG_COOL_TEMP.size(); b++) {

				eng_cool_temp_flt += CAN_ENG_COOL_TEMP.get(b);

			}
			eng_cool_temp_flt = eng_cool_temp_flt / CAN_ENG_COOL_TEMP.size();
			eng_cool_temp_str = Float.toString(eng_cool_temp_flt);
			readText.append("Coolant Temp ");
			readText.append(eng_cool_temp_str);
			readText.append("\n");

			for (int c = 0; c < CAN_FUEL_FLOW.size(); c++) {

				fuel_flow_flt += CAN_FUEL_FLOW.get(c);

			}
			fuel_flow_flt = fuel_flow_flt / CAN_ENG_COOL_TEMP.size();
			fuel_flow_str = Float.toString(fuel_flow_flt);
			readText.append("Fuel Flow ");
			readText.append(fuel_flow_str);
			readText.append("\n");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
