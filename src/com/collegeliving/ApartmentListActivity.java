package com.collegeliving;

import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class ApartmentListActivity extends LocationActivity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_grid);
		
	}
	
	private double getLatRange() {
		double lat = 0;
		double negLat = 0;
		double posLat = 0;
		double d = 16.0934; // 10 miles
		double r = 6371;
		if(getLocation() != null) {
			lat = getLatitude();
			posLat =  d/r + Math.toRadians(lat);
			negLat = -d/r + Math.toRadians(lat);
		}
		return posLat;
	}
	
	private double getLongRange() {
		double lon = 0;
		double negLon = 0;
		double posLon = 0;
		double d = 16.0934; // 10 miles
		double r = 6371;
		if(getLocation() != null) {
			lon = getLongitude();
			posLon = d/r + Math.toRadians(lon);
			negLon = -d/r + Math.toRadians(lon);
		}
		return posLon;
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
