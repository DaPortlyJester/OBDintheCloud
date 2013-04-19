package com.umich.umd.obdpractice;

import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class ManageConnection {

	// Debug tag for identifying from which activity debug message
	// originated
	private final static String DEBUG_TAG = "ManageConnect";

	// The SSID for the OBD Network
	private final static String OBD_SSID = "OBD";

	// The network ID before networkID was switched to OBD Network
	public int initialNetworkID;
	// Whether the network state was changed
	public boolean networkStateChanged = false;

	private ConnectivityManager conMgr;

	private NetworkInfo wifiConnectInfo;
	private NetworkInfo mobileNetInfo;

	private WifiManager wifiMgr;

	private WifiInfo wifiInfo;

	public boolean obdWIFIconnected(Context appContext) {

		// Grab connection information

		conMgr = (ConnectivityManager) appContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		wifiConnectInfo = conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		// Grab Wifi connection information
		wifiMgr = (WifiManager) appContext
				.getSystemService(Context.WIFI_SERVICE);
		wifiInfo = wifiMgr.getConnectionInfo();

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
					 * In Wifi Configuration, SSID surround with quotes Consider
					 * storing matched networkID so this process is not
					 * necessary every time log files are downloaded from
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
			return true;
		} else {
			return false;
		}
	}

	public boolean networkConnected(Context appContext) {

		conMgr = (ConnectivityManager) appContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		Log.d(DEBUG_TAG, "Net avail:"
				+ conMgr.getActiveNetworkInfo().isConnectedOrConnecting());

		mobileNetInfo = conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		wifiConnectInfo = conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if ((mobileNetInfo != null && mobileNetInfo.isConnectedOrConnecting())
				|| (wifiConnectInfo != null && wifiConnectInfo
						.isConnectedOrConnecting())) {
			Log.d(DEBUG_TAG, "Data Connection Available");
			return true;
		}

		return false;
	}
}
