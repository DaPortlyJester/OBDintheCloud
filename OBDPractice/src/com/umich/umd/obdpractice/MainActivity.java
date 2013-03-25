
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
	
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	
	private final static String DEBUG_TAG = "OBDMainAct";
	private final static String OBD_SSID = "OBD";
	
	private int initialNetworkID;
	private boolean networkStateChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void sendMessage(View view) 
    {
    	Intent intent = new Intent(this, DisplayMessageActivity.class);
    	startActivity(intent);    	
    }
    
    public void setupNetwork(View view)
    {
    	
    	ConnectivityManager conMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    	NetworkInfo wifiConnectInfo = conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    	
    	WifiManager wifiMgr = (WifiManager)getSystemService(Context.WIFI_SERVICE);
    	WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
    	
    	
    	
    	/*
    	 * Check if Wifi Connection is detected
    	 * If not, display alert dialog to switch to turn on Wifi
    	 * and select OBD network
    	 */
    	if(wifiConnectInfo.isConnected())
    	{
    		
    		/*
    		 * Check if current wifi network is OBD Network, if not
    		 * Check for the OBD network among current WifiConfigurations
    		 * If found, switch to OBD Network 
    		 */
    		
    		Log.d(DEBUG_TAG, "The current Wifi SSID is: " + wifiInfo.getSSID());
    		initialNetworkID = wifiInfo.getNetworkId();
    		
        	if( !wifiInfo.getSSID().equals(OBD_SSID))
        	{

        		List<WifiConfiguration> wifiList = wifiMgr.getConfiguredNetworks();
        		
        		for(WifiConfiguration result: wifiList) {
        			
        			if(result.SSID != null && result.SSID.equals("\"" + OBD_SSID + "\""))
        			wifiMgr.disconnect();
        			wifiMgr.enableNetwork(result.networkId, true);
        			wifiMgr.reconnect();
        			networkStateChanged = true;
        			
        		}
        	}
        	
        	// Switch to NetworkSetupActivity
        	Intent intent = new Intent(this,NetworkSetupActivity.class);
        	startActivity(intent);
    	}
    	else
    	{
    		
    		
    		AlertDialog.Builder builder =  new AlertDialog.Builder(this);
    		TextView noWifiMsg = new TextView(this);
    		noWifiMsg.setText("No Wifi Connection Found\nPlease go to Wifi Settings and\n" +
    				" choose the OBD Wifi Connection");
    		noWifiMsg.setGravity(Gravity.CENTER);
    		
    		builder.setView(noWifiMsg)
    				.setCancelable(false)
    				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							
						}
					});
    		
    		AlertDialog dialog = builder.create();
    		dialog.show();
    		
    	}
    }
    
    public void startLogging(View view)
    {
    	
    }
    
    public void monitorGryphon(View view)
    {
    	
    }
    
    public void connectCloud(View view)
    {
    	
    }
    
    public void viewStorage(View view)
    {
    	Intent intent = new Intent(this,ViewStoredLogsActivity.class);
    	startActivity(intent);	
    }

    
    
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(networkStateChanged) {
			WifiManager wifiMgr = (WifiManager)getSystemService(Context.WIFI_SERVICE);
			wifiMgr.disconnect();
			wifiMgr.enableNetwork(initialNetworkID, true);
			wifiMgr.reconnect();
		}
		
		super.onDestroy();
	}
    
}
