package com.collegeliving;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ChatScreen extends MessageSyncActivity {
	private int chat_with_id=0;
	static final int REFRESH = 1;
	private EditText message_box;
	private Button sendBtn;
	private ListView window;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_screen);
		
		String displayName = "";
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			chat_with_id = extras.getInt("chatWithUID");
			displayName = extras.getString("displayName");
		}
		getActionBar().setTitle("Chat with "+displayName);
		window = (ListView) findViewById(R.id.ChatWindow);
		message_box = (EditText) findViewById(R.id.message_sending);
		sendBtn = (Button) findViewById(R.id.send_btn);
		sendBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!message_box.getText().toString().equals(""))
					sendMsg();
			} 			
		});
		draw();
	}

	private void draw(){
		DatabaseHelper db = DatabaseHelper.getInstance(ChatScreen.this);
		ArrayList<MsgRecord> msgs = db.getConversation(this.getLoggedInUser(), chat_with_id);
		MessageBubbleAdapter adapter = (MessageBubbleAdapter) window.getAdapter();
		if(adapter == null) {
			adapter = new MessageBubbleAdapter(this,msgs);
			window.setAdapter(adapter);
		} else {			
			adapter.updateData(msgs);
		}
		setReadFlag();
	}
	
	private void setReadFlag() {
		DatabaseHelper db = DatabaseHelper.getInstance(this);
		db.setReadFlag(this.getLoggedInUser(), this.chat_with_id);
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
			SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String datetime = ft.format(new Date());
			DatabaseHelper db = DatabaseHelper.getInstance(ChatScreen.this);
			db.insertMessage(msg, user_id, user_id, chat_with_id, datetime);
			draw();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		ServerCallback msgResponse = new ServerCallback(){
			@Override
			public void Run(String response) {
				JSONObject json;
				try {
					json= new JSONObject(response);
					boolean success = json.getBoolean("success");
				} catch (JSONException e) {
					Toast.makeText(getApplicationContext(), "Can't connect to the server", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}
		};
		new ServerPost(json, msgResponse).execute("/collegeliving/post/message.php");
		message_box.setText("");
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

	@Override
	protected void onNewMessageReceive(JSONArray msgs) {
		if(msgs.length() > 0) {
			draw();
		}
	}
	
}