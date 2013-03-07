package com.collegeliving;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

import org.json.*;

public class ServerPost extends AsyncTask<String, Void, String> {
	
	private JSONObject data;
	private ServerCallback callback;
	
	public ServerPost(JSONObject data, ServerCallback callback) {
		super();
		this.data = data;
		this.callback = callback;
	}
	
	protected String doInBackground(String... params) {
		String url = params[0];
		HttpClient http = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://149.47.177.209/"+url);
		try {
			post.setEntity(new StringEntity(this.data.toString(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			Log.e(e.getClass().getName(), e.getMessage());
		}
		post.setHeader("Content-type", "application/json");
		HttpResponse response = null;
		try {
			response = http.execute(post);
		} catch (ClientProtocolException e) {
			Log.e(e.getClass().getName(), e.getMessage());
		} catch (IOException e) {
			Log.e(e.getClass().getName(), e.getMessage());
		}
		String responseString = null;
		try {
			responseString = EntityUtils.toString(response.getEntity());
		} catch (ParseException e) {
			Log.e(e.getClass().getName(), e.getMessage());
		} catch (IOException e) {
			Log.e(e.getClass().getName(), e.getMessage());
		}
		return responseString;
	}
	
	protected void onPostExecute(String param) {
		this.callback.Run(param);
	}
	
}
