package com.umich.umd.obdpractice;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

	// Debug tag for identifying from which activity debug message 
	// originated
	private final static String DEBUG_TAG = "OBDMainAct";
	// The SSID for the OBD Network
	private final static String OBD_SSID = "OBD";

	// The network ID before networkID was switched to OBD Network
	private int initialNetworkID;
	// Whether the network state was changed
	private boolean networkStateChanged = false;

	/**
	 * Called each time activity is created (including on screen orientation
	 * changed)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	/**
	 * Builds option menu on activity creation
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * When user clicks Download Log Files From Gryphon button detect the
	 * current wifi state and SSID connected If Wifi is not currently connected,
	 * alert the user to turn on Wifi and setup the OBD network. If Wifi is
	 * currently connected, but not connected to the OBD network, then store the
	 * current wifi configuration, and switch to the preconfigured OBD Wifi
	 * Network Then switch to the log file download activity
	 * 
	 * @param view
	 *            The view where the click originated from
	 */
	public void setupNetwork(View view) {

		// Grab connection information
		ConnectivityManager conMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo wifiConnectInfo = conMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		// Grab Wifi connection information
		WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

		/*
		 * Check if Wifi Connection is detected If not, display alert dialog to
		 * switch to turn on Wifi and select OBD network
		 */
		if (wifiConnectInfo.isConnected()) {

			/*
			 * Check if current wifi network is OBD Network, if not Check for
			 * the OBD network among current WifiConfigurations If found, switch
			 * to OBD Network
			 */

			Log.d(DEBUG_TAG, "The current Wifi SSID is: " + wifiInfo.getSSID());
			initialNetworkID = wifiInfo.getNetworkId();

			if (!wifiInfo.getSSID().equals(OBD_SSID)) {
				// Grab list of all current user configured networks
				List<WifiConfiguration> wifiList = wifiMgr
						.getConfiguredNetworks();

				// Iterate through configured networks, if OBD network is found
				// switch to it
				// Mark network state changed to true
				for (WifiConfiguration result : wifiList) {

					/*
					 * In Wifi Configuration, SSID surround with quotes
					 * Consider storing matched networkID so this process is
					 * not necessary every time log files are downloaded from
					 * Gryphon
					 */
					if (result.SSID != null
							&& result.SSID.equals("\"" + OBD_SSID + "\"")) {
						// Disconnect wifi before switching
						wifiMgr.disconnect();
						// Switch to matched SSID
						wifiMgr.enableNetwork(result.networkId, true);
						wifiMgr.reconnect();
						networkStateChanged = true;
					}
				}
			}

			// Switch to NetworkSetupActivity
			Intent intent = new Intent(this, NetworkSetupActivity.class);
			startActivity(intent);
		} else {

			/*
			 * If Wifi is not connected, build AlertDialog to alert user that
			 * they should configure network
			 */
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// TextView to hold alert message
			TextView noWifiMsg = new TextView(this);
			noWifiMsg
					.setText("No Wifi Connection Found\nPlease go to Wifi Settings and\n"
							+ " choose the OBD Wifi Connection");
			// Center message in TextView
			noWifiMsg.setGravity(Gravity.CENTER);
			// Add TextView to AlertDialog and setup Buttons with listener
			builder.setView(noWifiMsg)
					.setCancelable(false)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();

								}
							});

			// Create/instantiate actual AlertDialog
			AlertDialog dialog = builder.create();
			// Show the dialog to the user, spawned on separate thread
			dialog.show();
		}
	}

	/**
	 * Unimplemented
	 * 
	 * @param view
	 */
	public void startLogging(View view) {

	}

	/**
	 * Unimplemented
	 * 
	 * @param view
	 */
	public void monitorGryphon(View view) {

	}

	/**
	 * When the user clicks the connect to Cloud Button, switch to the CloudFile
	 * Upload Activity
	 * 
	 * @param view
	 *            The view where the click originated
	 */
	public void connectCloud(View view) {
		Intent intent = new Intent(this, CloudFileUpload.class);
		startActivity(intent);
	}

	/**
	 * When the user clicks the View Stored Files Button, switch to the
	 * ViewStoredLogs Activity
	 * 
	 * @param view
	 *            The view where the click originated
	 */
	public void viewStorage(View view) {
		Intent intent = new Intent(this, ViewStoredLogsActivity.class);
		startActivity(intent);
	}

	/**
	 * Override of onDestroy Detect if the wifi network state was changed during
	 * the applications use If it network state was changed, return it to its
	 * original state Afterwards, return to normal onDestroy process
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (networkStateChanged) {
			WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			wifiMgr.disconnect();
			wifiMgr.enableNetwork(initialNetworkID, true);
			wifiMgr.reconnect();
		}

		super.onDestroy();
	}

}
