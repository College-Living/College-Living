package com.collegeliving;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.*;

public class MainScreen extends LocationActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_screen);
		setRoomiesTile();
		setPadTile();
		setMyProfileTile();
		setSettingsTile();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_login_screen, menu);
		return true;
	}

	
	public void setRoomiesTile() {
		RelativeLayout roomiesTile = (RelativeLayout) findViewById(R.id.roomie_tile);
		roomiesTile.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent roomiesActivity = new Intent(v.getContext(), RoomiesList.class);
				startActivity(roomiesActivity);
			}
		});
	} 
	
	
	public void setPadTile() {
		RelativeLayout padsTile = (RelativeLayout) findViewById(R.id.pads_tile);
		padsTile.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent padsActivity = new Intent(v.getContext(), ApartmentList.class);
				startActivity(padsActivity);
			}
		});
	}
	
	public void setMyProfileTile(){
		RelativeLayout myProfileTile = (RelativeLayout) findViewById(R.id.profile_tile);
		myProfileTile.setOnClickListener(new OnClickListener() {
			public void onClick(View v){
				Intent profileActivity = new Intent(v.getContext(), MyProfileScreen.class);
				startActivity(profileActivity);
			}
		});
	}
	
	public void setSettingsTile() {
		RelativeLayout settingsTile = (RelativeLayout) findViewById(R.id.setting_tile);
		settingsTile.setOnClickListener(new OnClickListener() {
			public void onClick(View v){
				Intent settingsActivity = new Intent(v.getContext(),Settings.class);
				startActivity(settingsActivity);
			}
		});
	}
	
	public void onBackPressed() {
		
	}

}
