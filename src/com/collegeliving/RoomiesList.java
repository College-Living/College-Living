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

public class RoomiesList extends MessageSyncActivity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_grid);
		getRoomies();
	}
	
	private void draw() {
		ArrayList<Tile> tiles = new ArrayList<Tile>();
		DatabaseHelper db = DatabaseHelper.getInstance(this);
		ArrayList<RoomieRecord> records = db.getRoomies(this.getLoggedInUser());
		for(int i = 0; i < records.size(); i++) {
			RoomieRecord record = records.get(i);
			String secondary = "";
			if(record.unreadCount == 1) secondary = "<b>"+record.unreadCount+" New Message";
			else if(record.unreadCount > 1) secondary = "<b>"+record.unreadCount+" New Messages";
			tiles.add(new Tile(record.uID, record.displayName, Math.round(record.compatScore*100)+"% compatible", secondary, record.thumbnailURL));
		}
		GridView grid = (GridView) findViewById(R.id.grid);
		grid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				URIGridAdapter adapter = (URIGridAdapter) parent.getAdapter();
				Tile roomie = adapter.getItem(position);
				int roomieID = roomie.id;
				Log.i("roomieID", roomieID+"");
				Intent roomieScreen = new Intent(v.getContext(), RoomieScreen.class);
				roomieScreen.putExtra("RoomieID", roomieID);
				startActivity(roomieScreen);
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
	
	
	private void getRoomies() {
		JSONObject json = new JSONObject();
		DatabaseHelper db = DatabaseHelper.getInstance(this);
		try {
			json.put("lat", getLatitude());
			json.put("long", getLongitude());
			json.put("uid", this.getLoggedInUser());
			json.put("chat_uids", new JSONArray(db.getRoomiesWithChatHistory(this.getLoggedInUser())));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ServerCallback callback = new ServerCallback() {
			public void Run(String response) {
				DatabaseHelper db = DatabaseHelper.getInstance(RoomiesList.this);
				db.clearRoomies();
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
	
	protected void onResume() {
		super.onResume();
		getRoomies();
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
	    	getRoomies();
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
			getRoomies();
		}
	}

	@Override
	protected void onNewMessageReceive(JSONArray msgs) {
		if(msgs.length() > 0) getRoomies();
		
	}
}