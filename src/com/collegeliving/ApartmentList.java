package com.collegeliving;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

public class ApartmentList extends LocationActivity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_grid);
		getLocalApartments();
	}
	
	private void getLocalApartments() {
		double distance = 10.0;
		double radius = 3963.1676;
		JSONObject json = new JSONObject();
		try {
			json.put("lat", getLatitude());
			json.put("long", getLongitude());
			json.put("delta", (double) distance/radius); 
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ServerCallback callback = new ServerCallback() {
			public void Run(String response) {
				ArrayList<ApartmentList.Apartment> apartments = new ArrayList<ApartmentList.Apartment>();
				try {
					Log.i("json", response);
					JSONObject json = new JSONObject(response);
					boolean success = json.getBoolean("success");
					if(success) {
						JSONArray jApartments = json.getJSONArray("apartments");
						for(int i = 0; i < jApartments.length(); i++) {
							JSONObject apartment = jApartments.getJSONObject(i);
							String aptName = apartment.getString("AptName");
							String email = apartment.getString("Email");
							String phone = apartment.getString("Phone");
							String website = apartment.getString("AptWebsite");
							String price = apartment.getString("PriceRange");
							String image = apartment.getString("Thumbnail");
							String distance = apartment.getString("Distance");
							distance = String.format("%.2fmi", Double.valueOf(distance));
							apartments.add(new Apartment(aptName, image, phone, email, website, price, distance));
						}
						showApartments(apartments);
					} else {
						Log.e("error", json.getString("error"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}			
		};
		new ServerPost(json, callback).execute("/collegeliving/get/apartments.php");
	}
	
	private void showApartments(ArrayList<ApartmentList.Apartment> apartments) {
		GridView grid = (GridView) findViewById(R.id.grid);
		URIGridAdapter adapter = new URIGridAdapter(this, apartments);
		grid.setAdapter(adapter);
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
	
	static public class Apartment {
		public String imageURL;
		public String displayName;
		public String phone;
		public String email;
		public String website;
		public String priceRange;
		public String distance;
		public Apartment(String displayName, String imageURL, String phone, String email, String website, String priceRange, String distance) {
			this.displayName = displayName;
			this.phone = phone;
			this.email = email;
			this.website = website;
			this.priceRange = priceRange;
			this.imageURL = imageURL;
			this.distance = distance;
		}
	}
	
	@Override
	public void onLocationChanged(Location arg0) {
		super.onLocationChanged(arg0);
		if(arg0 != null) {
			getLocalApartments();
		}
	}
}
