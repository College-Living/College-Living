package com.collegeliving;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyProfileScreen extends LocationActivity{
	private int uid; 
	private int email_hide; private int phone_hide; private int aboutme_hide;
	TextView email_toggle;
	TextView phone_toggle;
	TextView aboutme_toggle;
	TextView email_content;
	TextView phone_content;
	TextView aboutme_content;
	TextView displayname_content;
	ImageView img;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_my_profile);
		uid = this.getLoggedInUser();
		email_toggle = (TextView) findViewById(R.id.mp_Email);
		phone_toggle = (TextView) findViewById(R.id.mp_number);
		aboutme_toggle = (TextView) findViewById(R.id.mp_about_me);
		email_content = (TextView) findViewById(R.id.myprofile_email);
		phone_content = (TextView) findViewById(R.id.myprofile_pnumber);
		aboutme_content = (TextView) findViewById(R.id.myprofile_about_me);
		displayname_content = (TextView) findViewById(R.id.myprofile_display_name);
		img = (ImageView) findViewById(R.id.profile_img);
	}
	protected void onResume(){
		super.onResume();
		loadMyProfile();
		set_email_toggle();
		set_phone_toggle();
		set_aboutme_toggle();
	}
	
	private void loadMyProfile(){
		JSONObject json = new JSONObject();
		try {
			json.put("uid", uid);
			json.put("method", "load_info");
		} catch (JSONException e){
			e.printStackTrace();
		}
		ServerCallback loadResponse = new ServerCallback(){
			@Override
			public void Run(String response) {
				try {
					JSONObject json = new JSONObject(response);
					boolean success = json.getBoolean("success");
					if(success){
						String email = json.getString("email");
						String phone = json.getString("phone");
						String aboutme = json.getString("aboutme");
						String displayname = json.getString("displayname");
						String photoUrl = json.getString("Thumbnail");
						email_hide = json.getInt("email_toggle");
						phone_hide = json.getInt("phone_toggle");
						aboutme_hide = json.getInt("aboutme_toggle");
						email_content.setText(email);
						phone_content.setText(phone);
						aboutme_content.setText(Html.fromHtml(aboutme));
						displayname_content.setText(displayname);
						if(photoUrl.length() != 0)
							new ImageLoader(img, "http://"+ServerPost.server_ip+"/collegeliving/"+photoUrl);
						grabToggleState();
					}else{
						Toast.makeText(getApplicationContext(), "Can't connect to the server", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
		new ServerPost(json, loadResponse).execute("/collegeliving/post/myprofile.php");
	}
	
	
	private void grabToggleState(){
		ImageView email_toggle = (ImageView) findViewById(R.id.email_padlock);
		ImageView phone_toggle = (ImageView) findViewById(R.id.phone_padlock);
		ImageView about_toggle = (ImageView) findViewById(R.id.about_padlock);
		Drawable visible = getResources().getDrawable(R.drawable.visible);
		Drawable hidden = getResources().getDrawable(R.drawable.hidden);
		if(email_hide==1)
			email_toggle.setImageDrawable(hidden);
		else
			email_toggle.setImageDrawable(visible);
		if(phone_hide==1)
			phone_toggle.setImageDrawable(hidden);
		else
			phone_toggle.setImageDrawable(visible);
		if(aboutme_hide==1)
			about_toggle.setImageDrawable(hidden);
		else
			about_toggle.setImageDrawable(visible);
	}
	
	private JSONObject setJSONToggle(String toggle, String value){
		JSONObject json = new JSONObject();
		try {
			json.put("method", "set_toggle");
			json.put(toggle, value);
			json.put("uid",uid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	private void set_email_toggle(){
		email_toggle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(email_hide==0)
					email_hide=1;
				else
					email_hide=0;
				JSONObject json = setJSONToggle("email",  ""+email_hide);
				ServerCallback response = new ServerCallback(){
					@Override
					public void Run(String p) {
						JSONObject json;
						try {
							json = new JSONObject(p);
							boolean success = json.getBoolean("success");
							if(success){
								grabToggleState();
								String toast;
								if(email_hide==1)
									toast = "Email is hidden";
								else
									toast = "Email is public";
								Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
							}else{
								Toast.makeText(getApplicationContext(), "Can't connect to the server", Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				};
				new ServerPost(json, response).execute("/collegeliving/post/myprofile.php");
			}
		});
	}
	
	private void set_phone_toggle(){
		phone_toggle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(phone_hide==0)
					phone_hide=1;
				else
					phone_hide=0;
				JSONObject json = setJSONToggle("phone",  ""+phone_hide);
				ServerCallback response = new ServerCallback(){
					@Override
					public void Run(String p) {
						JSONObject json;
						try {
							json = new JSONObject(p);
							boolean success = json.getBoolean("success");
							if(success){
								grabToggleState();
								String toast;
								if(phone_hide==1)
									toast = "Phone# is hidden";
								else
									toast = "Phone# is public";
								Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
							}else{
								Toast.makeText(getApplicationContext(), "Can't connect to the server", Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					
				};
				new ServerPost(json, response).execute("/collegeliving/post/myprofile.php");
			}
		});
	}
	
	private void set_aboutme_toggle(){
		aboutme_toggle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(aboutme_hide==0)
					aboutme_hide=1;
				else
					aboutme_hide=0;
				JSONObject json = setJSONToggle("aboutme",  ""+aboutme_hide);
				ServerCallback response = new ServerCallback(){
					@Override
					public void Run(String p) {
						JSONObject json;
						try {
							json = new JSONObject(p);
							boolean success = json.getBoolean("success");
							if(success){
								grabToggleState();
								String toast;
								if(aboutme_hide==1)
									toast = "About Me is hidden";
								else
									toast = "About Me is public";
								Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
							}else{
								Toast.makeText(getApplicationContext(), "Can't connect to the server", Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				};
				new ServerPost(json, response).execute("/collegeliving/post/myprofile.php");
			}
		});
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
