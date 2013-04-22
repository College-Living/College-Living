package com.collegeliving;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ApartmentScreen extends LocationActivity {
	ApartmentRecord apartment;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_profile);
		Bundle extras = getIntent().getExtras();
		int aptID = 0;
		if(extras != null)
			aptID = extras.getInt("AptID");
		apartment = getApartmentInfo(aptID);
		if(apartment != null)
			loadApartmentInfo(apartment.aptName, apartment.website, apartment.email, apartment.phone, apartment.aboutApt, apartment.thumbnailURL);
		else
			this.finish();
	}
	
	public ApartmentRecord getApartmentInfo(int aptID) {
		DatabaseHelper db = DatabaseHelper.getInstance(this);
		return db.getPad(aptID);
	}
	
	public void loadApartmentInfo(String aptName, String website, String email, String phone, String about, String photoUrl) {
		TextView tv_displayName = (TextView) findViewById(R.id.display_name);
		TextView tv_about = (TextView) findViewById(R.id.profile_about_me);
		ImageView img = (ImageView) findViewById(R.id.profile_img);
		if(aptName.equals(""))
			tv_displayName.setText(Html.fromHtml("<i>Not provided</i>"));
		else
			tv_displayName.setText(aptName);
		if(about.equals(""))
			tv_about.setText(Html.fromHtml("<i>Not provided</i>"));
		else
			tv_about.setText(Html.fromHtml(about));
		if(photoUrl.length() != 0)
			new ImageLoader(img, "http://"+ServerPost.server_ip+"/collegeliving/"+photoUrl);
	}
	
	private void showWebsite() {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(apartment.website));
		startActivity(browserIntent);
	}
	
	private void emailApt() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("plain/text");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] { apartment.email });
		startActivity(Intent.createChooser(intent, ""));
	}
	
	private void callApt() {
		String uri = "tel:" + apartment.phone.trim() ;
		 Intent intent = new Intent(Intent.ACTION_DIAL);
		 intent.setData(Uri.parse(uri));
		 startActivity(intent);
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
	        
	    case R.id.website:
	    	showWebsite();
	    	break;
	    case R.id.email:
	    	emailApt();
	    	break;
	    case R.id.phone:
	    	callApt();
	    	break;

	    default:
	        return super.onOptionsItemSelected(item);
	    }
	    return true;
	}
	
}
