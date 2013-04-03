package com.collegeliving;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.*;

public class LoginScreen extends Activity {
	private final Activity currentActivity;
	
	public LoginScreen() {
		super();
		currentActivity = this;
	}
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_screen);
		
		
		/* JSONObject data = new JSONObject();
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
		new ServerPost(null, afterPost).execute("collegeliving/getpics.php"); */
		setRegisterBtn();
	
		setLoginBtn();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_login_screen, menu);
		return true;
	}
	
	public void setRegisterBtn() {
		Button registerBtn = (Button) findViewById(R.id.registerBtn);
		registerBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent registerActivity = new Intent(v.getContext(), Registration.class);
				startActivity(registerActivity);
			}
		});
	}


	public void setLoginBtn() {
		Button loginBtn = (Button) findViewById(R.id.loginBtn);
		loginBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				login();
			}
		});
	}

	private void login() {
		EditText emailText = (EditText) findViewById(R.id.email);
		EditText passwordText = (EditText) findViewById(R.id.password);
		String email = emailText.getText().toString();
		String password = passwordText.getText().toString();
		JSONObject json = new JSONObject();
		ServerCallback callback = new ServerCallback() {

			@Override
			public void Run(String response) {
				// TODO Auto-generated method stub
				try {
					JSONObject json = new JSONObject(response);
					boolean success = json.getBoolean("success");
					String userID = json.getString("userID");
					if(success) {
						//set user id in user preference 
						loginSuccessful();
					} else
						showLoginError();
				} catch (JSONException e){
					e.printStackTrace();
				}
			}
			
		};
		try {
			json.put("email", email);
	
			json.put("password", password);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		new ServerPost(json, callback).execute("/collegeliving/post/login.php");
		
		
	}


	private void loginSuccessful(){
		Intent home = new Intent(this,MainScreen.class);
		startActivity(home);
	}

	private void showLoginError(){
		Toast.makeText(this, "Invaild Email or Password", Toast.LENGTH_LONG).show();
	}
}
