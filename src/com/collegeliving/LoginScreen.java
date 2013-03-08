package com.collegeliving;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
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
				String si = "";
				ArrayList<ImageView> thumbnails = new ArrayList<ImageView>();
				ImageView view = null;
				try {
					response = new JSONObject(p);
					s = response.getJSONArray("urls");
					for(int i = 0; i < s.length(); i++) {
						si = s.getString(i);
						view = new ImageView(currentActivity);
						thumbnails.add(view);
						new DownloadImage(view).execute(si);
						RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
							    RelativeLayout.LayoutParams.WRAP_CONTENT,
							    RelativeLayout.LayoutParams.WRAP_CONTENT);

						currentActivity.addContentView(view, lp);
					}
					//TextView t = (TextView) currentActivity.findViewById(R.id.HttpTest);
					//t.setText(si);
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
