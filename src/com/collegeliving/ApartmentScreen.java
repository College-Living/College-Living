package com.collegeliving;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
		DatabaseHelper db = DatabaseHelper.getInstance(this);
		ApartmentRecord apartment = db.getPad(aptID);
		if(apartment != null)
			loadApartmentInfo(apartment.aptName, apartment.website, apartment.email, apartment.phone, apartment.aboutApt, apartment.thumbnailURL);
		else
			this.finish();
	}
	
	public void loadApartmentInfo(String aptName, String website, String email, String phone, String about, String photoURL) {
		TextView tv_displayName = (TextView) findViewById(R.id.display_name);
		TextView tv_website = (TextView) findViewById(R.id.profile_website);
		TextView tv_email = (TextView) findViewById(R.id.profile_email);
		TextView tv_phone = (TextView) findViewById(R.id.profile_pnumber);
		TextView tv_about = (TextView) findViewById(R.id.profile_about_me);
		
		if(email.equals(""))
			((View) tv_email.getParent()).setVisibility(View.GONE);
		else
			tv_email.setText(email);
		if(aptName.equals(""))
			((View) tv_displayName.getParent()).setVisibility(View.GONE);
		else
			tv_displayName.setText(aptName);
		if(website.equals(""))
			((View) tv_website.getParent()).setVisibility(View.GONE);
		else
			tv_website.setText(website);
		if(phone.equals(""))
			((View) tv_phone.getParent()).setVisibility(View.GONE);
		else
			tv_phone.setText(phone);
		if(about.equals(""))
			((View) tv_about.getParent()).setVisibility(View.GONE);
		else
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
