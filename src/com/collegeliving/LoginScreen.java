package com.collegeliving;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
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
				String s;
				JSONObject response;
				try {
					response = new JSONObject(p);
					s = response.getString("title");
					TextView t = (TextView) currentActivity.findViewById(R.id.HttpTest);
					t.setText(s);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return;
			}
		};
		new ServerPost(data, afterPost).execute("test.php");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_login_screen, menu);
		return true;
	}

}
