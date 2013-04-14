package com.umich.umd.obdpractice;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableNotifiedException;

import com.google.api.client.http.InputStreamContent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class PutHTTPTask extends AsyncTask<String, Void, String> {

	// Debug tag for identifying from which activity debug message
	// originated
	private static final String DEBUG_TAG = "PutAsyncUpload";

	/** E-mail address of the service account. */
	private static String SERVICE_ACCOUNT_EMAIL;

	/** Google Cloud Storage URI */
	private static final String GCS_URI = "http://storage.googleapis.com/";

	private static final String HOST = "storage.googleapis.com/obd_data";
	/** Bucket to list. */
	private static final String BUCKET_NAME = "obd_data";

	/** Global configuration of Google Cloud Storage OAuth 2.0 scope. */
	private static final String STORAGE_SCOPE = "oauth2:https://www.googleapis.com/auth/devstorage.read_write";

	/*
	 * /** Global instance of the HTTP transport. private static final
	 * HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	 * 
	 * /** Global instance of the JSON factory. private static final JsonFactory
	 * JSON_FACTORY = new JacksonFactory();
	 * 
	 * /** Information for making application based calls to Google APIs private
	 * static final String CALLBACK_URL = "urn:ietf:wg:oauth:2.0:oob"; private
	 * static final String SIMPLE_API_KEY =
	 * "AIzaSyCQ492-1MwRlAI2zKRCv0kAXfFHQX9Q0S4";
	 * 
	 * private static final String CLIENT_ID =
	 * "809398875393.apps.googleusercontent.com"; private static final String
	 * CLIENT_SECRET =
	 * "{\"installed\":{\"auth_uri\":\"https://accounts.google.com/o/oauth2/auth\","
	 * +
	 * "\"token_uri\":\"https://accounts.google.com/o/oauth2/token\",\"client_email\":\"\",\"redirect_uris\":"
	 * +
	 * "[\"urn:ietf:wg:oauth:2.0:oob\",\"oob\"],\"client_x509_cert_url\":\"\",\"client_id\":"
	 * +
	 * "\"809398875393.apps.googleusercontent.com\",\"auth_provider_x509_cert_url\":"
	 * + "\"https://www.googleapis.com/oauth2/v1/certs\"}}";
	 */

	private final Context cloudContext;
	private final CloudFileUpload cupActivity;

	public PutHTTPTask(CloudFileUpload cupAct, Context cloudActContext,
			String authenticationEmail) {
		super();
		SERVICE_ACCOUNT_EMAIL = authenticationEmail;
		this.cloudContext = cloudActContext;
		cupActivity = cupAct;
	}

	@Override
	protected String doInBackground(String... fileNames) {

		String output = null;

		for (String fN : fileNames) {
			try {
				output = fileUpload(fN, cloudContext);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return output;
	}

	/**
	 * Method to perform upload of file to the Google Cloud Storage Executes
	 * token authentication, put file into cloud, and grabbing response
	 * 
	 * @param fileName
	 *            The name of the file to upload to the cloud
	 * @param cloudActContext
	 *            The Conext of the CloudUploadFile Activity
	 * @return The response from the post request in string format
	 * @throws IOException
	 */
	public String fileUpload(String fileName, Context cloudActContext)
			throws IOException {
		
		/*
		 * Preconditions.checkArgument( !SERVICE_ACCOUNT_EMAIL.startsWith("[["),
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

		// String authorizeURL = new GoogleAuthorizationRequestURL(CLIENT_ID,
		// CALLBACK_URL,STORAGE_SCOPE);

		// Fetch Google Authentication Token
		String token = fetchToken();

		Log.d(DEBUG_TAG, fileName);
		// input stream from file to upload to cloud
		FileInputStream fis = null;

		// Will hold size of the file
		long byteCount;

		// InputStreamEntity for AndroidHttpClient
		// InputStreamEntity fileStreamE = null;

		// HttpURL Connection and DataOutputStream for
		// Streaming large file chunks in streaming mode
		HttpURLConnection httpConnection = null;
		DataOutputStream outputStream = null;

		int bytesRead, bytesAvailable, bufferSize;
		byte[] upBuffer;
		int maxBufferSize = 1 * 1024;

		final SimpleDateFormat sdf = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss z",Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		URL url = null;

		/**
		 * ---- Beginning of Actual Connection and Upload Process ----
		 */
		try {

			// Open input stream from to upload
			fis = cloudActContext.openFileInput(fileName);

			// Get the size of the file for transmission purposes
			byteCount = fis.getChannel().size();
			Log.d(DEBUG_TAG, "File Size: " + byteCount);

			// Create new HttpContent entity from file Input Stream
			// for AndroidHttpClient
			//fileStreamE = new InputStreamEntity(fis, -1);

			String URI = GCS_URI + BUCKET_NAME + "/" + fileName;
			url = new URL(URI);
			URI uri = new URI(url.getProtocol(), url.getUserInfo(),
					url.getHost(), url.getPort(), url.getPath(),
					url.getQuery(), url.getRef());
			url = uri.toURL();
			URI = uri.toString();

			// /**
			// * Setup HttpClient and Post connection with appropriate headers
			// for
			// * Cloud Connection
			// */
			// AndroidHttpClient client = AndroidHttpClient.newInstance("OBD");
			// Log.d(DEBUG_TAG, URI);
			// HttpPut put = new HttpPut(URI);
			// Log.d(DEBUG_TAG, "OAuth " + token);
			// put.setHeader("Authorization", "OAuth " + token);
			// put.setHeader("Content-Length", Long.toString(byteCount));
			// Calendar cal = new
			// GregorianCalendar(TimeZone.getTimeZone("GMT"));
			// cal.setTimeZone(TimeZone.getTimeZone("GMT"));
			// String dateStr = cal.getTime().toString();
			// Log.d(DEBUG_TAG, dateStr);
			// put.setHeader("Date", dateStr);
			// Log.d(DEBUG_TAG, HOST);
			// put.setHeader("Host", HOST);
			// put.setHeader("Content-Type", "text/plain");
			// put.setHeader("x-goog-api-version", "2");
			// put.setEntity(fileStreamE);
			// Log.d(DEBUG_TAG, put.toString());
			// Log.d(DEBUG_TAG, put.getRequestLine().toString());
			//
			// HttpResponse response = client.execute(put);

			/**
			 * HttpURLConnection based version. Stopped due to problems
			 * appending data to put request
			 */
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.setRequestMethod("PUT");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			Log.d(DEBUG_TAG, "OAuth" + token);
			httpConnection
					.addRequestProperty("Authorization", "OAuth " + token);
			httpConnection.addRequestProperty("Content-Length",
					Long.toString(byteCount));
			// format time using SimpleDateFormat from above
			String dateStr = sdf.format(new Date());
			Log.d(DEBUG_TAG, dateStr);
			httpConnection.addRequestProperty("Date", dateStr);
			httpConnection.addRequestProperty("Host", HOST);
			httpConnection.addRequestProperty("Content-Type", "text/plain");
			httpConnection.addRequestProperty("x-goog-api-version", "2");
			// httpConnection.addRequestProperty("Connection", "Keep-Alive");

			outputStream = new DataOutputStream(
					httpConnection.getOutputStream());
			bytesAvailable = fis.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			upBuffer = new byte[bufferSize];

			bytesRead = fis.read(upBuffer, 0, bufferSize);
			Log.d(DEBUG_TAG + " upLength", bytesAvailable + "");

			try {
				while (bytesRead > 0) {
					try {
						outputStream.write(upBuffer, 0, bufferSize);
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
						return "OutOfMemoryError on Upload";
					}
					bytesAvailable = fis.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fis.read(upBuffer, 0, bufferSize);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return "Unhandled Exception Error";
			} finally {
				outputStream.flush();
				outputStream.close();
				outputStream = null;
			}

			/**
			 * HttpURLConnection version of response handler
			 * Consider using httpConnection.getContent() in future versions
			 */
			int respCode = httpConnection.getResponseCode();
			Log.i("GCS_RC", "" + respCode);
			if (respCode == 200) {
				// Get URLConnection input stream
				InputStream is = httpConnection.getInputStream();
				// Use thread-safe string buffer to build response
				StringBuffer responseString = new StringBuffer("");
				BufferedReader buffer = new BufferedReader(
						new InputStreamReader(is));
				String s = "";
				while ((s = buffer.readLine()) != null)
					responseString.append(s + "\n");
				return responseString.toString();
			} else if (respCode == 401) {
				GoogleAuthUtil.invalidateToken(cupActivity, token);
				onError("Server auth error, please try again.", null);
				Log.e(DEBUG_TAG, "Server auth error: "
						+ httpConnection.getResponseMessage());
				return null;
			} else {
				Log.e(DEBUG_TAG, "Server returned the following error code: "
						+ respCode);
				Log.e(DEBUG_TAG, "Connection Error: "
						+ httpConnection.getResponseMessage());
				return null;
			}

			/**
			 * AndroidHttpClient version of Response Handler
			 */
			/*int respCode = response.getStatusLine().getStatusCode();
			if (respCode == 200) {
				InputStream is = response.getEntity().getContent();
				StringBuffer responseString = new StringBuffer("");
				BufferedReader buffer = new BufferedReader(
						new InputStreamReader(is));
				String s = "";
				while ((s = buffer.readLine()) != null)
					responseString.append(s + "\n");
				return responseString.toString();
			} else if (respCode == 401) {
				GoogleAuthUtil.invalidateToken(cupActivity, token);
				onError("Server auth error, please try again.", null);
				Log.e(DEBUG_TAG, "Server auth error: "
						+ response.getStatusLine().getReasonPhrase());
				return null;
			} else {
				Log.e(DEBUG_TAG, "Server returned the following error code: "
						+ respCode);
				return null;
			}*/
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} finally {
			if (fis != null)
				fis.close();
		}
		return null;

	}

	/**
	 * Grab Google OAuth 2.0 Authentication token using Google Play Services API
	 * for Google Cloud Storage
	 * 
	 * @return The authentication token string to append to request header/query
	 * @throws IOException
	 *             Don't know when an IOException might be thrown
	 */
	protected String fetchToken() throws IOException {

		try {
			return GoogleAuthUtil.getTokenWithNotification(cloudContext,
					SERVICE_ACCOUNT_EMAIL, STORAGE_SCOPE, null,
					makeCallback(SERVICE_ACCOUNT_EMAIL));
		} catch (UserRecoverableNotifiedException userRecoverableException) {
			// Unable to authenticate, but the user can fix this.
			// Forward the user to the appropriate activity.
			onError("Could not fetch token.", null);
		} catch (GoogleAuthException fatalException) {
			onError("Unrecoverable error " + fatalException.getMessage(),
					fatalException);
		}
		return null;
	}

	/**
	 * Callback function to CloudFileUploadActivity if authentication process
	 * throws an unrecoverable error
	 * 
	 * @param sERVICE_ACCOUNT_EMAIL2
	 *            The email account to be authenticated
	 * @return An intent containing information of activity to make callback to
	 */
	private Intent makeCallback(String sERVICE_ACCOUNT_EMAIL2) {
		Intent intent = new Intent();
		intent.setAction(" om.umich.umd.obdpractice.Callback");
		intent.putExtra(CloudManager.ACCOUNT_TAG, sERVICE_ACCOUNT_EMAIL2);
		return intent;
	}

	protected void onError(String msg, Exception e) {
		if (e != null) {
			Log.e(DEBUG_TAG, "Exception: ", e);
		}
		cupActivity.show(msg); // will be run in UI thread
	}

	/**
	 * Note: Make sure that the receiver can be called from outside the app. You
	 * can do that by adding android:exported="true" in the manifest file.
	 */
	public static class CallbackReceiver extends BroadcastReceiver {
		public static final String TAG = "CallbackReceiver";

		@Override
		public void onReceive(Context context, Intent callback) {
			Bundle extras = callback.getExtras();
			Intent intent = new Intent(context, CloudFileUpload.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtras(extras);
			Log.i(TAG, "Received broadcast. Resurrecting activity");
			context.startActivity(intent);
		}
	}

}
