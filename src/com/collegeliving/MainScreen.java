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
		Toast.makeText(this, String.valueOf(this.getLoggedInUser()), Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_login_screen, menu);
		return true;
	}

	public void setPadTile() {
		LinearLayout padsTile = (LinearLayout) findViewById(R.id.pads_tile);
		padsTile.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent padsActivity = new Intent(v.getContext(), apartment_list_activity);
				startActivity(padsActivity);
			}
		});
	}
	
	public void setSettingsTile() {
		LinearLayout settingsTile = (LinearLayout) findViewById(R.id.setting_tile);
		settingsTile.setOnClickListener(new OnClickListener() {
			public void onClick(View v){
				Intent settingsActivity = new Intent(v.getContext(),Settings.class);
				startActivity(settingsActivity);
			}
		});
	}

}
