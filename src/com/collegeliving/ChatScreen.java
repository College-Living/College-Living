package com.collegeliving;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class ChatScreen extends LocationActivity {
	private int chat_with_id=0;
	static final int REFRESH = 1;
	private Handler handler;
	private TimerTask task;
	private Timer timer;
	EditText message_box;
	Button sendBtn;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_screen);
		Bundle extras = getIntent().getExtras();
		if(extras != null)
			chat_with_id = extras.getInt("chatWithUID");
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what) {
				case REFRESH:
					getMsgFromServer();
				}
			}
		};
		task = new TimerTask() {
			public void run() {
				ChatScreen.this.getHandler().sendEmptyMessage(ChatScreen.REFRESH);
			}
		};
		timer = new Timer();
		timer.scheduleAtFixedRate(task, 0, 5000);
		message_box = (EditText) findViewById(R.id.message_sending);
		sendBtn = (Button) findViewById(R.id.send_btn);
		sendBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!message_box.getText().toString().equals(""))
					sendMsg();
			} 			
		});
	}
	
	public Handler getHandler() {
		return handler;
	}
	

	private void draw(){
		DatabaseHelper db = DatabaseHelper.getInstance(ChatScreen.this);
		ArrayList<MsgRecord> msgs = db.getConversation(this.getLoggedInUser(), chat_with_id);
		Log.i("size", ""+msgs.size());
		ListView window = (ListView) findViewById(R.id.ChatWindow);
		window.setAdapter(null);
		MessageBubbleAdapter adapter = new MessageBubbleAdapter(this,msgs);
		window.setAdapter(adapter);
	}
	private void sendMsg(){
		int user_id = this.getLoggedInUser();
		String msg = message_box.getText().toString();
		JSONObject json = new JSONObject();
		try {
			json.put("method", "send");
			json.put("FromUID", user_id);
			json.put("ToUID", chat_with_id);
			json.put("message", msg);
			SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
			String datetime = ft.format(new Date());
			DatabaseHelper db = DatabaseHelper.getInstance(ChatScreen.this);
			db.insertMessage(msg, user_id, user_id, chat_with_id, datetime);
			draw();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ServerCallback msgResponse = new ServerCallback(){
			@Override
			public void Run(String response) {
				JSONObject json;
				Log.i("response", response);
				try {
					json= new JSONObject(response);
					boolean success = json.getBoolean("success");
					if(success){

					}else{
						Toast.makeText(getApplicationContext(), "Can't connect to the server", Toast.LENGTH_SHORT).show();
						
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		new ServerPost(json, msgResponse).execute("/collegeliving/post/message.php");
		
		message_box.setText("");
	}
	private void getMsgFromServer(){
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
				Log.i("response", response);
				try {
					json = new JSONObject(response);
					boolean success = json.getBoolean("success");
					if(success){
						JSONArray msg = json.getJSONArray("msg_obj");
						for(int i=0; i<msg.length(); i++){
							String content = msg.getJSONObject(i).getString("Content");
							Log.i("msg", content);
							int from = msg.getJSONObject(i).getInt("FromUID");
							int to = msg.getJSONObject(i).getInt("ToUID");
							String datetime = msg.getJSONObject(i).getString("Timestamp");
							DatabaseHelper db = DatabaseHelper.getInstance(ChatScreen.this);
							db.insertMessage(content, ChatScreen.this.getLoggedInUser(), from, to, datetime);
						}//insert to local db finished.
						draw();
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
	
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	    switch (item.getItemId()) 
	    {
	    case android.R.id.home: 
	        onBackPressed();
	        break;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	    return true;
	}
	
	protected void onPause() {
		super.onPause();
		timer.cancel();
	}
	
}