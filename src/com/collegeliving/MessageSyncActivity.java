package com.collegeliving;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

abstract public class MessageSyncActivity extends LocationActivity {
	
	protected static final int REFRESH = 1;
	public Handler handler;
	private TimerTask timer_task;
	private Timer timer;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what) {
				case REFRESH:
					MessageSyncActivity.this.retreiveNewMessages();
					break;
				}
			}
		};
	}
	
	abstract protected void onNewMessageReceive(JSONArray msgs);
	
	public void retreiveNewMessages() {
		int user_id = this.getLoggedInUser();
		JSONObject json = new JSONObject();
		try {
			json.put("method", "retrieve");
			json.put("FromUID", user_id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ServerCallback msgResponse = new ServerCallback(){
			@Override
			public void Run(String response) {
				JSONObject json;
				try {
					json = new JSONObject(response);
					Log.i("json", response);
					boolean success = json.getBoolean("success");
					if(success){
						JSONArray msgs = json.getJSONArray("msg_obj");
						for(int i=0; i<msgs.length(); i++){
							JSONObject msg = msgs.getJSONObject(i);
							String content = msg.getString("Content");
							int from = msg.getInt("FromUID");
							int to = msg.getInt("ToUID");
							String datetime = msg.getString("Timestamp");
							DatabaseHelper db = DatabaseHelper.getInstance(MessageSyncActivity.this);
							db.insertMessage(content, MessageSyncActivity.this.getLoggedInUser(), from, to, datetime);
						}
						onNewMessageReceive(msgs);
					}
				} catch (JSONException e) {
					Toast.makeText(getApplicationContext(), "Can't connect to the server", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}	
			}
		};
		new ServerPost(json, msgResponse).execute("/collegeliving/post/message.php");
	}
	
	private void startTimer() {
		timer_task = new TimerTask() {
			@Override
			public void run() {
				MessageSyncActivity.this.handler.sendEmptyMessage(MessageSyncActivity.REFRESH);
			}			
		};
		timer = new Timer();
		timer.scheduleAtFixedRate(timer_task, 0, 5000);
	}
	
	protected void onResume() {
		super.onResume();
		startTimer();
	}
	
	protected void onPause() {
		super.onPause();
		timer_task.cancel();
		timer.cancel();
		timer.purge();
	}
}
