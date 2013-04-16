package com.collegeliving;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
		double distance = 10.0; // TO-DO: update to shared preference
		double radius = 3959;
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
				ArrayList<Tile> apartmentTiles = new ArrayList<Tile>();
				try {
					Log.i("json", response);
					JSONObject json = new JSONObject(response);
					boolean success = json.getBoolean("success");
					if(success) {
						JSONArray jApartments = json.getJSONArray("apartments");
						for(int i = 0; i < jApartments.length(); i++) {
							JSONObject apartment = jApartments.getJSONObject(i);
							int aptID = apartment.getInt("AptID");
							String aptName = apartment.getString("AptName");
							String email = apartment.getString("Email");
							String phone = apartment.getString("Phone");
							String website = apartment.getString("AptWebsite");
							String price = apartment.getString("PriceRange");
							String image = apartment.getString("Thumbnail");
							String distance = apartment.getString("Distance");
							distance = String.format("%.2fmi", Double.valueOf(distance));
							apartmentTiles.add(new Tile(aptID, aptName, price + ' ' + distance, image));
						}
						showApartments(apartmentTiles);
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
	
	private void showApartments(ArrayList<Tile> apartmentTiles) {
		GridView grid = (GridView) findViewById(R.id.grid);
		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
			}
			
		});
		URIGridAdapter adapter = new URIGridAdapter(this, apartmentTiles);
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
	
	@Override
	public void onLocationChanged(Location arg0) {
		super.onLocationChanged(arg0);
		if(arg0 != null) {
			getLocalApartments();
		}
	}
}