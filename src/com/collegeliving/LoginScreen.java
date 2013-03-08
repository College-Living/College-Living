package com.collegeliving;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.*;

public class LoginScreen extends Activity {
	private Activity currentActivity;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_screen);
		JSONObject data = new JSONObject();
		currentActivity = this;
		try {
			data.put("first_name", "Derek");
			data.put("last_name", "Overlock");
			data.put("age", 20);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ServerCallback afterPost = new ServerCallback() {
			public void Run(String p) {
				JSONArray s;
				JSONObject response;
				GridView gridView = (GridView) currentActivity.findViewById(R.id.grid);
				ArrayList<String> urls = new ArrayList<String>();
				try {
					response = new JSONObject(p);
					s = response.getJSONArray("urls");
					for(int i = 0; i < s.length(); i++) {
						urls.add(s.getString(i));
					}
					gridView.setAdapter(new URIGridAdapter(currentActivity, urls));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return;
			}
		};
		new ServerPost(null, afterPost).execute("collegeliving/getpics.php");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_login_screen, menu);
		return true;
	}

}
