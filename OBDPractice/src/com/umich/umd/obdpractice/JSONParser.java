package com.umich.umd.obdpractice;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// import android.content.Intent;
import android.util.Log;

public class JSONParser {

	//JSON Node names
	private static final String TAG_FILES = "files";
	
	// files JSONObject
	private JSONObject filesJSONObject = null;	
	// JSONArray of log files
	private JSONArray files = null;
	
	ArrayList<String> fList = new ArrayList<String>();
	
	public ArrayList<String> jsonToList(String jsonString) {
		
		try{
			// Convert the jsonString to a Java JSONObject
			filesJSONObject = new JSONObject(jsonString);
			// Grab the files JSON Array from the JSONObject
			files = filesJSONObject.getJSONArray(TAG_FILES);
			
			// Store each element of the array into the ArrayList
			for(int i=0; i < files.length(); i++) {
				// temporary store first filename
				String tmpFN = files.getString(i);
				// Check that filename is long enough (notEmpty)
				if(tmpFN.length() > 27) {
					// Grab the BASE_NAME from the filename
					tmpFN = tmpFN.substring(0, 27);
					// If BASE_NAME is not already in list, add
					if(!fList.contains(tmpFN)) {
						fList.add(tmpFN);
					}
				} 
			}
		} catch(JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}
		
		
		return fList;
		
	}
	
	
}
