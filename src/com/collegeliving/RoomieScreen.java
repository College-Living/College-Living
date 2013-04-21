package com.collegeliving;


import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class RoomieScreen extends LocationActivity {
	private int roomieID = 0;
	private String displayName = "";
	private RoomieRecord roomie;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_profile);
		Bundle extras = getIntent().getExtras();
		roomieID = 0;
		if(extras != null) {
			roomieID = extras.getInt("RoomieID");
		}
		roomie = getRoomieInfo(roomieID);
		if(roomie != null)
			loadRoomieInfo(roomie.displayName, roomie.email, roomie.phone, roomie.aboutMe, roomie.thumbnailURL);
		else
			this.finish();
	}
	
	public RoomieRecord getRoomieInfo(int roomieID) {
		DatabaseHelper db = DatabaseHelper.getInstance(this);
		RoomieRecord roomie = db.getRoomie(this.getLoggedInUser(), roomieID);
		return roomie;
	}
	
	public void loadRoomieInfo(String displayName, String email, String phone, String about, String photoUrl) {
		TextView tv_displayName = (TextView) findViewById(R.id.display_name);
		TextView tv_email = (TextView) findViewById(R.id.profile_email);
		TextView tv_phone = (TextView) findViewById(R.id.profile_pnumber);
		TextView tv_about = (TextView) findViewById(R.id.profile_about_me);
		ImageView img = (ImageView) findViewById(R.id.profile_img);
		if(email.equals(""))
			tv_email.setText(Html.fromHtml("<i>Not provided</i>"));
		else
			tv_email.setText(email);
		if(displayName.equals(""))
			tv_displayName.setText(Html.fromHtml("<i>Not provided</i>"));
		else
			tv_displayName.setText(displayName);
		if(phone.equals(""))
			tv_phone.setText(Html.fromHtml("<i>Not provided</i>"));
		else
			tv_phone.setText(phone);
		if(about.equals(""))
			tv_about.setText(Html.fromHtml("<i>Not provided</i>"));
		else
			tv_about.setText(Html.fromHtml(about));
		if(photoUrl.length() != 0)
			new ImageLoader(img, "http://"+ServerPost.server_ip+"/collegeliving/"+photoUrl);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_roomie_screen, menu);
	    return true;
	}
	
	private void startChat() {
		Intent chat = new Intent(this, ChatScreen.class);
		chat.putExtra("chatWithUID", roomieID);
		chat.putExtra("displayName", roomie.displayName);
		startActivity(chat);
	}
	
	private void confirmBlockUser() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Block "+roomie.displayName+"?");
		 
		// set dialog message
		dialog
			.setMessage("Are you sure you want to block this user?")
			.setCancelable(false)
			.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					blockUser();
				}
			  })
			.setNegativeButton("No",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				}
			});
			AlertDialog alertDialog = dialog.create();
			alertDialog.show();
	}
	
	private void blockUser() {
		int userID = roomie.uID;
		int currentUser = getLoggedInUser();
		JSONObject json = new JSONObject();
		try {
			json.put("uid", currentUser);
			json.put("blocked_uid", userID);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ServerCallback callback = new ServerCallback() {
			@Override
			public void Run(String response) {
				RoomieScreen.this.finish();
			}
		};
		new ServerPost(json, callback).execute("/collegeliving/post/block_user.php");
	}
	
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	    switch (item.getItemId()) 
	    {
	    case android.R.id.home: 
	        onBackPressed();
	        break;
	    case R.id.block:
	    	confirmBlockUser();
	    	break;
	    case R.id.message:
	    	startChat();
	    	break;

	    default:
	        return super.onOptionsItemSelected(item);
	    }
	    return true;
	}
	
}
