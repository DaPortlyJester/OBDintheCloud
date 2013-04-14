package com.umich.umd.obdpractice;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

public class SelectFilesFragment extends DialogFragment{
	
	private final static String LIST_KEY = "filesList";
	private final static String DEBUG_TAG = "SelectFilesDialog";

	public ArrayList<String> fileList;
	
	public int fileIndex;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		Bundle fileBundle =  this.getArguments();
		fileList = fileBundle.getStringArrayList(LIST_KEY);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		builder.setTitle(R.string.select_file)
		.setItems(fileList.toArray(new CharSequence[fileList.size()]), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				fileIndex = which;
				Log.d(DEBUG_TAG, "File Index " + fileIndex);
				String fileName = fileList.get(which);
				Log.d(DEBUG_TAG, "File Name: " + fileName);
				CloudFileUpload.setCurr_log_file_base_name(fileName);
				
			}
		});
		
		return builder.create();
	}


	

}
