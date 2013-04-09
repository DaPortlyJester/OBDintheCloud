package com.umich.umd.obdpractice;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

public class PutHTTPTask extends AsyncTask<String, Void, String> {

	// Debug tag for identifying from which activity debug message
	// originated
	private static final String DEBUG_TAG = "PutAsyncUpload";

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

	private static final String CALLBACK_URL = "urn:ietf:wg:oauth:2.0:oob";
	private static final String SIMPLE_API_KEY = "AIzaSyCQ492-1MwRlAI2zKRCv0kAXfFHQX9Q0S4";
	
	private static final String CLIENT_ID = "809398875393.apps.googleusercontent.com";
	private static final String CLIENT_SECRET = "{\"installed\":{\"auth_uri\":\"https://accounts.google.com/o/oauth2/auth\"," +
			"\"token_uri\":\"https://accounts.google.com/o/oauth2/token\",\"client_email\":\"\",\"redirect_uris\":" +
			"[\"urn:ietf:wg:oauth:2.0:oob\",\"oob\"],\"client_x509_cert_url\":\"\",\"client_id\":" +
			"\"809398875393.apps.googleusercontent.com\",\"auth_provider_x509_cert_url\":" +
			"\"https://www.googleapis.com/oauth2/v1/certs\"}}";

	private final Context cloudContext;

	public PutHTTPTask(Context cloudContext) {
		super();
		this.cloudContext = cloudContext;
	}

	@Override
	protected String doInBackground(String... fileNames) {

		String output = null;

		for (String fN : fileNames) {
			try {
				output = fileUpload(fN, cloudContext);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// TODO Auto-generated method stub
		return null;
	}

	public String fileUpload(String fileName, Context cloudActContext)
			throws IOException {
		
		String authorizeURL = new GoogleAuthorizationRequestURL(CLIENT_ID,
				CALLBACK_URL,STORAGE_SCOPE);

		Log.d(DEBUG_TAG, fileName);
		FileInputStream fis = cloudActContext.openFileInput(fileName);

		InputStream in = null;
		long byteCount;

		/*
		 * Preconditions .checkArgument(
		 * !SERVICE_ACCOUNT_EMAIL.startsWith("[["),
		 * "Please enter your service account e-mail from the Google APIs " +
		 * "Console to the SERVICE_ACCOUNT_EMAIL constant in %s",
		 * CloudManager.class.getName());
		 * Preconditions.checkArgument(!BUCKET_NAME.startsWith("[["),
		 * "Please enter your desired Google Cloud Storage bucket name " +
		 * "to the BUCKET_NAME constant in %s", CloudManager.class.getName());
		 * String p12Content = Files.readFirstLine(new File("key.p12"),
		 * Charset.defaultCharset());
		 * Preconditions.checkArgument(!p12Content.startsWith("Please"),
		 * p12Content);
		 */

		try {

			// Build service account credential.
			/*
			 * GoogleCredential credential = new
			 * GoogleCredential.Builder().setTransport(HTTP_TRANSPORT)
			 * .setJsonFactory(JSON_FACTORY)
			 * .setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
			 * .setServiceAccountScopes(STORAGE_SCOPE).build();
			 */

			in = new BufferedInputStream(fis);

			InputStreamContent mediaContent = new InputStreamContent(
					"application/octet-steam", in);

			byteCount = fis.getChannel().size();
			Log.d(DEBUG_TAG, "File Size: " + byteCount);
			mediaContent.setLength(byteCount);

			String URI = GCS_URI + BUCKET_NAME;
			HttpRequestFactory requestFactory = HTTP_TRANSPORT
					.createRequestFactory();
			GenericUrl url = new GenericUrl(URI);

			HttpRequest request = requestFactory.buildPutRequest(url,
					mediaContent);
			request.
			HttpResponse response = request.execute();

			return response.parseAsString();

			// StorageObject objectMetadata = null;

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
		return null;

	}

	protected void onPostExecute(String output) {

	}

}
