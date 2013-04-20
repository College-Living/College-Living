package com.collegeliving;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class RoomiesList extends LocationActivity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_grid);
		getLocalRoomies();
	}
	
	private void draw() {
		ArrayList<Tile> tiles = new ArrayList<Tile>();
		DatabaseHelper db = DatabaseHelper.getInstance(this);
		ArrayList<RoomieRecord> records = db.getRoomies();
		for(int i = 0; i < records.size(); i++) {
			RoomieRecord record = records.get(i);
			tiles.add(new Tile(record.uID, record.displayName, Math.round(record.compatScore*100)+"% compatible", "", record.thumbnailURL));
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
		URIGridAdapter adapter = new URIGridAdapter(this, tiles);
		grid.setAdapter(adapter);
	}
	
	
	private void getLocalRoomies() {
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
				ArrayList<Tile> roomiesTiles = new ArrayList<Tile>();
				DatabaseHelper db = DatabaseHelper.getInstance(RoomiesList.this);
				db.clearRoomies();
				try {
					Log.i("json", response);
					JSONObject json = new JSONObject(response);
					Log.i("sql", json.getString("query"));
					boolean success = json.getBoolean("success");
					if(success) {
						JSONArray jRoomies = json.getJSONArray("roomies");
						for(int i = 0; i < jRoomies.length(); i++) {
							JSONObject roomie = jRoomies.getJSONObject(i);
							int UID = roomie.getInt("UID");
							String uName = roomie.getString("uName");
							String email = roomie.getString("Email");
							String phone = roomie.getString("Phone");
							double cScore = roomie.getDouble("CompScore");
							String image = roomie.getString("Thumbnail");
							String aboutMe = roomie.getString("AboutMe");
							db.insertOrUpdateRoomie(UID, uName, phone, email, aboutMe, image, cScore);
						}
						draw();
					} else {
						Log.e("error", json.getString("error"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}			
		};
		new ServerPost(json, callback).execute("/collegeliving/get/roomies.php");
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
	    	getLocalRoomies();
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
			getLocalRoomies();
		}
	}
}