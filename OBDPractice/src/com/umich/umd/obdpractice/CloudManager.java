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

import android.util.Log;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.StorageObject;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;

public class CloudManager {

	// Debug tag for identifying from which activity debug message
	// originated
	private final static String DEBUG_TAG = "CloudManag";

	/** E-mail address of the service account. */
	private static final String SERVICE_ACCOUNT_EMAIL = "Derelle.Redmond@gmail.com";
	
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
	
/*	private FileWriteChannel writeChannel = null;
	FileService fileService =  FileServiceFactory.getFileService();
	private OutputStream os = null;
	private static final Logger log =  Logger.getLogger(CloudManager.class.getName());*/

	
	
	public void fileInsert(String fileName) throws IOException {

		File file = new File(fileName);
		FileInputStream fis;

		InputStream in = null;
		long byteCount;

		/*Preconditions
				.checkArgument(
						!SERVICE_ACCOUNT_EMAIL.startsWith("[["),
						"Please enter your service account e-mail from the Google APIs "
								+ "Console to the SERVICE_ACCOUNT_EMAIL constant in %s",
						CloudManager.class.getName());
		Preconditions.checkArgument(!BUCKET_NAME.startsWith("[["),
				"Please enter your desired Google Cloud Storage bucket name "
						+ "to the BUCKET_NAME constant in %s",
				CloudManager.class.getName());
		String p12Content = Files.readFirstLine(new File("key.p12"),
				Charset.defaultCharset());
		Preconditions.checkArgument(!p12Content.startsWith("Please"),
				p12Content);*/

		try {
			
	        // Build service account credential.
	        GoogleCredential credential = 
	          new GoogleCredential.Builder().setTransport(HTTP_TRANSPORT)
	            .setJsonFactory(JSON_FACTORY)
	            .setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
	            .setServiceAccountScopes(STORAGE_SCOPE).build();
	            
	        String URI = GCS_URI + BUCKET_NAME;
	        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(credential);
	        GenericUrl url = new GenericUrl(URI);
	        /*//HttpContent content;
			HttpRequest request =  requestFactory.buildPutRequest(url, content);
	        HttpResponse response = request.execute();*/
	        
	            
			in = new BufferedInputStream(fis = new FileInputStream(file));
			InputStreamContent mediaContent = new InputStreamContent(
					"application/octet-steam", in);

			byteCount = fis.getChannel().size();
			Log.d(DEBUG_TAG, "File Size: " + byteCount);

			mediaContent.setLength(byteCount);
			StorageObject objectMetadata = null;

			// Storage.Objects.Insert insertObject = storage.objects().insert(
			// "obd_data", objectMetadata, mediaContent);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}

}
