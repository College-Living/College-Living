package com.collegeliving;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
	
	public void loadApartmentInfo(String aptName, String website, String email, String phone, String about, String photoUrl) {
		TextView tv_displayName = (TextView) findViewById(R.id.display_name);
		TextView tv_website = (TextView) findViewById(R.id.profile_website);
		TextView tv_email = (TextView) findViewById(R.id.profile_email);
		TextView tv_phone = (TextView) findViewById(R.id.profile_pnumber);
		TextView tv_about = (TextView) findViewById(R.id.profile_about_me);
		ImageView img = (ImageView) findViewById(R.id.profile_img);
		if(email.equals(""))
			tv_email.setText(Html.fromHtml("<i>Not provided</i>"));
		else
			tv_email.setText(email);
		if(aptName.equals(""))
			tv_displayName.setText(Html.fromHtml("<i>Not provided</i>"));
		else
			tv_displayName.setText(aptName);
		if(website.equals(""))
			tv_website.setText(Html.fromHtml("<i>Not provided</i>"));
		else
			tv_website.setText(website);
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
