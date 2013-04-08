package com.collegeliving;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Settings extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_screen);
		setLogoutBtn();
	}
	
	public void setLogoutBtn(){
		Button LogoutBtn = (Button) findViewById(R.id.logout_button);
		LogoutBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedPreferences preferences = getSharedPreferences("UserSharedPreferences", 0);
				preferences.edit().remove("UID").commit();
				Intent login = new Intent(v.getContext(), LoginScreen.class);
				startActivity(login);
			}
		});
	}
}
