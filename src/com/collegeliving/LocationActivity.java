package com.collegeliving;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public abstract class LocationActivity extends Activity implements LocationListener {

	private LocationManager lm;
	private final int MIN_UPDATE_TIME = 1000 * 1 * 2; // every 5 minutes
	private final int MIN_DISTANCE = 402; // .25 mile = 402; 1 MILE = 1609.34 meter
	private final int REFRESH = 1;
	private Location location;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		getGPS();
		setInitLocation();
	}
	
	public void getGPS() {
		boolean providerFound = false;
		if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			registerGPS(LocationManager.GPS_PROVIDER);
			providerFound = true;
		}
		if(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			registerGPS(LocationManager.NETWORK_PROVIDER);
			providerFound = true;
		}
		if(!providerFound) {
			Intent locationService = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(locationService);
			getGPS();
		}		
	}
	
	public void setInitLocation() {
		Location tmp = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		Location tmp1 = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if(tmp != null) {
			setLocation(tmp);
			updateDatabase();
		} else if(tmp1 != null) {
			setLocation(tmp1);
			updateDatabase();
		}
	}
	
	public Location getLocation() {
		return location;
	}
	
	public double getLatitude() {
		if(getLocation() == null)
			return 0;
		else 
			return getLocation().getLatitude();
	}
	
	public double getLongitude() {
		if(getLocation() == null)
			return 0;
		else 
			return getLocation().getLongitude();
	}
	
	public void registerGPS(String provider) {
		lm.requestLocationUpdates(provider, MIN_UPDATE_TIME, MIN_DISTANCE, this);
	}

	public void onLocationChanged(Location arg0) {
		if(arg0 != null) {
			setLocation(arg0);
			updateDatabase();
		}
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
	
	protected void onPause() {
		super.onPause();
		lm.removeUpdates(this);
	}
	
	protected void onStop() {
		super.onStop();
		lm.removeUpdates(this);
	}
	
	protected int getLoggedInUser() {
		String USERPREF = "UserSharedPreferences";
		SharedPreferences userShared = getSharedPreferences(USERPREF, Activity.MODE_PRIVATE);
		int UID = userShared.getInt("UID", 0);
		return UID;
	}
	
	protected void updateDatabase() {
		String URL = "/collegeliving/post/location.php";
		try {
			JSONObject json = new JSONObject();
			json.put("lat", this.getLatitude());
			json.put("long", this.getLongitude());
			json.put("uid", getLoggedInUser());
			new ServerPost(json, null).execute(URL);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	

}
