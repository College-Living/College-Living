package com.collegeliving;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class ChatScreen extends LocationActivity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_screen);
		getMsgFromServer();
		
	}
	private void sendMsg(){
		
	}
	private void getMsgFromServer(){
		SharedPreferences preferences = getSharedPreferences("UserSharedPreferences", 0);
		int user_id = preferences.getInt("UID", -1);
		JSONObject json = new JSONObject();
		try {
			json.put("method", "retrieve");
			json.put("uid", user_id);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ServerCallback msgResponse = new ServerCallback(){
			@Override
			public void Run(String response) {
				JSONObject json;
				try {
					json = new JSONObject(response);
					boolean success = json.getBoolean("success");
					if(success){
						JSONArray msg = json.getJSONArray("msg_obj");
						for(int i=0; i<msg.length(); i++){
							String content = msg.getJSONObject(i).getString("Content");
							int from = msg.getJSONObject(i).getInt("FromUID");
							int to = msg.getJSONObject(i).getInt("ToUID");
							String datetime = msg.getJSONObject(i).getString("Timestamp");
							DatabaseHelper db = DatabaseHelper.getInstance(null);
							db.insertMessage(content, from, to, datetime);
						}//insert to local db finished.
						
						
					}else{
						Toast.makeText(getApplicationContext(), "Can't connect to the server", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}	
			}
		};
		new ServerPost(json, msgResponse).execute("/collegeliving/post/message.php");
	}
	
	
	
}
