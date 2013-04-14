package com.umich.umd.obdpractice;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.internal.au;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;


public class CloudManager {
	

	// Debug tag for identifying from which activity debug message
	// originated
	private final static String DEBUG_TAG = "CloudManage";

	/** E-mail address of the service account. */
	private static String SERVICE_ACCOUNT_EMAIL;
	public final static String ACCOUNT_TAG = "Account_Email"; 
	private final static String DEFAULT_EMAIL = "Derelle.Redmond@gmail.com";
	
	/** Google Cloud Storage URI */
	private static final String GCS_URI = "http://storage.googleapis.com/";

	/** Bucket to list. */
	private static final String BUCKET_NAME = "obd_data";

	/** Global configuration of Google Cloud Storage OAuth 2.0 scope. */
	private static final String STORAGE_SCOPE = "https://www.googleapis.com/auth/devstorage.read_write";

	/** Global instance of the HTTP transport. */
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	
	protected CloudFileUpload cupActivity;
	
	/**
	 * Constructor for sending Cloud authentication account information.
	 * If a null value is passed, uses default email address 
	 * @param cloudFileUpload 
	 * 
	 * @param authentication_account The string name of the account to authenticate with
	 * 
	 */
	public CloudManager(CloudFileUpload cloudFileUpload, String authentication_account) {
		cupActivity = cloudFileUpload;
		if(!(authentication_account == null))
			SERVICE_ACCOUNT_EMAIL = authentication_account;
		else
			SERVICE_ACCOUNT_EMAIL = DEFAULT_EMAIL;
	}

	public void fileInsert(String fileName, Context cloudActContext) throws IOException {

		Log.d(DEBUG_TAG, fileName);
		
		PutHTTPTask upFileTask = new PutHTTPTask(cupActivity, cloudActContext,SERVICE_ACCOUNT_EMAIL);
		upFileTask.execute(new String[] {fileName,"parsed" + fileName});
		
	}

}
