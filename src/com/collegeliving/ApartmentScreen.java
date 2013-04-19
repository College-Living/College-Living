package com.collegeliving;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

public class ApartmentScreen extends LocationActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_apartment_profile);
		Bundle extras = getIntent().getExtras();
		int aptID = 0;
		if(extras != null)
			aptID = extras.getInt("AptID");
		getApartmentInfo(aptID);
	}
	
	public void getApartmentInfo(int aptID) {
		ServerCallback callback = new ServerCallback() {
			public void Run(String response) {
				try {
					Log.i("json", response);
					JSONObject json = new JSONObject(response);
					JSONObject apartment = json.getJSONObject("apartment");
					String aptName = apartment.getString("AptName");
					String website = apartment.getString("AptWebsite");
					String email = apartment.getString("Email");
					String phone = apartment.getString("Phone");
					String about = apartment.getString("AptIntro");
					String photoURL = apartment.getString("Thumbnail");
					loadApartmentInfo(aptName, website, email, phone, about, photoURL);
				} catch(JSONException e) {
					e.printStackTrace();
				}
			}
		};
		
		JSONObject json = new JSONObject();
		try {
			json.put("AptID", aptID);
			new ServerPost(json, callback).execute("/collegeliving/get/apartment.php");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void loadApartmentInfo(String aptName, String website, String email, String phone, String about, String photoURL) {
		TextView tv_displayName = (TextView) findViewById(R.id.display_name);
		TextView tv_website = (TextView) findViewById(R.id.profile_website);
		TextView tv_email = (TextView) findViewById(R.id.profile_email);
		TextView tv_phone = (TextView) findViewById(R.id.profile_pnumber);
		TextView tv_about = (TextView) findViewById(R.id.profile_about_me);
		
		tv_displayName.setText(aptName);
		tv_website.setText(website);
		tv_email.setText(email);
		tv_phone.setText(phone);
		tv_about.setText(about);
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
