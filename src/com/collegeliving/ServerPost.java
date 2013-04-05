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
	
	private JSONObject data = null;
	private ServerCallback callback;
	final String server_ip = "149.47.177.209";
	
	public ServerPost(JSONObject data, ServerCallback callback) {
		super();
		this.data = data;
		this.callback = callback;
	}
	
	@Override
	protected String doInBackground(String... params) {
		String url = params[0];
		String responseString = "";
		HttpClient http = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://"+this.server_ip+"/"+url);
		try {
			if(this.data != null) {
				post.setEntity(new StringEntity(this.data.toString(), "UTF-8"));
				post.setHeader("Content-type", "application/json");
			}
			HttpResponse response = http.execute(post);
			responseString = EntityUtils.toString(response.getEntity());
		} catch (UnsupportedEncodingException e) {
			Log.e(e.getClass().getName(), e.getMessage());
		} catch (ClientProtocolException e) {
			Log.e(e.getClass().getName(), e.getMessage());
		} catch (IOException e) {
			Log.e(e.getClass().getName(), e.getMessage());
		} catch (ParseException e) {
			Log.e(e.getClass().getName(), e.getMessage());
		}
		return responseString;
	}
	
	@Override
	protected void onPostExecute(String param) {
		if(this.callback != null)
			this.callback.Run(param);
	}

	
}
