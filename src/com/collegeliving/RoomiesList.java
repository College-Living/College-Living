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

public class RoomiesList extends LocationActivity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_grid);
		getLocalRoomies();
	}
	
	private void getLocalRoomies() {
		double distance = 10.0; // TO-DO: update to shared preference
		double radius = 3959;
		JSONObject json = new JSONObject();
		try {
			json.put("lat", getLatitude());
			json.put("long", getLongitude());
			json.put("uid", this.getLoggedInUser());
			json.put("delta", (double) distance/radius); 
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ServerCallback callback = new ServerCallback() {
			public void Run(String response) {
				ArrayList<Tile> roomiesTiles = new ArrayList<Tile>();
				try {
					Log.i("json", response);
					JSONObject json = new JSONObject(response);
					boolean success = json.getBoolean("success");
					if(success) {
						JSONArray jRoomies = json.getJSONArray("roomies");
						for(int i = 0; i < jRoomies.length(); i++) {
							JSONObject roomie = jRoomies.getJSONObject(i);
							int UID = roomie.getInt("UID");
							String uName = roomie.getString("uName");
							String email = roomie.getString("Email");
							String phone = roomie.getString("Phone");
							String cScore = roomie.getString("CompScore");
							String image = roomie.getString("Thumbnail");
							roomiesTiles.add(new Tile(UID, uName, cScore, "", image));
						}
						showRoomies(roomiesTiles);
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
	
	private void showRoomies(ArrayList<Tile> roomieTiles) {
		GridView grid = (GridView) findViewById(R.id.grid);
		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
			}
			
		});
		URIGridAdapter adapter = new URIGridAdapter(this, roomieTiles);
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
			getLocalRoomies();
		}
	}
}