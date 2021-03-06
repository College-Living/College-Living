package com.collegeliving;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class ApartmentList extends LocationActivity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowTitleEnabled(false);*/
		setContentView(R.layout.activity_grid);
		getLocalApartments();
	}
	
	private void draw() {
		ArrayList<Tile> tiles = new ArrayList<Tile>();
		DatabaseHelper db = DatabaseHelper.getInstance(this);
		ArrayList<ApartmentRecord> records = db.getPads();
		for(int i = 0; i < records.size(); i++) {
			ApartmentRecord record = records.get(i);
			tiles.add(new Tile(record.aptID, record.aptName, record.price, String.format("%.2fmi", record.distance), record.thumbnailURL));
		}
		GridView grid = (GridView) findViewById(R.id.grid);
		grid.setAdapter(null);
		grid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				URIGridAdapter adapter = (URIGridAdapter) parent.getAdapter();
				Tile apartment = adapter.getItem(position);
				int aptID = apartment.id;
				Intent aptScreen = new Intent(v.getContext(), ApartmentScreen.class);
				aptScreen.putExtra("AptID", aptID);
				startActivity(aptScreen);
			}
		});
		URIGridAdapter adapter = (URIGridAdapter) grid.getAdapter();
		if(adapter == null) {
			adapter = new URIGridAdapter(this, tiles);
			grid.setAdapter(adapter);
		} else {
			adapter.updateData(tiles);
		}
	}
	
	private void getLocalApartments() {
		JSONObject json = new JSONObject();
		try {
			json.put("lat", getLatitude());
			json.put("long", getLongitude());
			json.put("uid", this.getLoggedInUser());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ServerCallback callback = new ServerCallback() {
			public void Run(String response) {
				DatabaseHelper db = DatabaseHelper.getInstance(ApartmentList.this);
				db.clearPads();
				try {
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
							String aboutApt = apartment.getString("AptIntro");
							String price = apartment.getString("PriceRange");
							String image = apartment.getString("Thumbnail");
							double lat = apartment.getDouble("Lat");
							double lon = apartment.getDouble("Long");
							double distance = apartment.getDouble("Distance");
							db.insertOrUpdatePad(aptID, aptName, price, distance, lon, lat, website, phone, email, aboutApt, image);
						}
						draw();
					} else {
						//error
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}			
		};
		new ServerPost(json, callback).execute("/collegeliving/get/apartments.php");
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_apartment_list, menu);
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	    switch (item.getItemId()) 
	    {
	    case android.R.id.home: 
	        onBackPressed();
	        break;
	    case R.id.refresh:
	    	getLocalApartments();
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