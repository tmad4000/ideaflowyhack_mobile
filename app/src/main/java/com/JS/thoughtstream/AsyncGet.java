package com.JS.thoughtstream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class AsyncGet extends AsyncTask<String, String, String> {
	private final static String TAG = "AsyncGet";
	
	private AsyncGetCallback mCallback;
	public AsyncGet(AsyncGetCallback callback) {
		mCallback = callback;
	}
	
	@Override
	protected String doInBackground(String... urls) {
		String responseString = "";
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		String url = urls[0];
		Log.i(TAG, "url: " + url);
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				responseString = builder.toString();
				Log.i(TAG, "Success, recieved OK 200");
			} else {
				Log.e("Getter", "Failed to connect");
			//	cancel(true);
			}
		
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return responseString;
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		mCallback.callback(result);
	}
}
