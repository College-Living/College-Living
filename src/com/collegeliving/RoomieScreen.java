package com.collegeliving;


import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class RoomieScreen extends LocationActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_profile);
		Bundle extras = getIntent().getExtras();
		int roomieID = 0;
		if(extras != null)
			roomieID = extras.getInt("RoomieID");
		getRoomieInfo(roomieID);
	}
	
	public void getRoomieInfo(int roomieID) {
		DatabaseHelper db = DatabaseHelper.getInstance(this);
		RoomieRecord roomie = db.getRoomie(roomieID);
		if(roomie != null)
			loadRoomieInfo(roomie.displayName, roomie.email, roomie.phone, roomie.aboutMe, roomie.thumbnailURL);
		else
			this.finish();
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
	
}
