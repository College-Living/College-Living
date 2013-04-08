package com.collegeliving;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Settings extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_settings_screen);
		setLogoutBtn();
		setEmailOnchange();
	}
	
	public void setEmailOnchange(){
		EditText email_setting = (EditText) findViewById(R.id.Email_setting);
		email_setting.addTextChangedListener(new TextWatcher() {
	        @Override
	        public void afterTextChanged(Editable s) {
	            // TODO Auto-generated method stub
	        }
	        @Override
	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	            // TODO Auto-generated method stub
	        }
	        @Override
	        public void onTextChanged(CharSequence s, int start, int before, int count) {
	        	Toast.makeText(null, "email changed!", Toast.LENGTH_LONG).show();
	        } 
	    });
	}
	public void setLogoutBtn(){
		Button LogoutBtn = (Button) findViewById(R.id.logout_button);
		LogoutBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences preferences = getSharedPreferences("UserSharedPreferences", 0);
				preferences.edit().remove("UID").commit();
				Intent login = new Intent(v.getContext(), LoginScreen.class);
				startActivity(login);
			}
		});
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
}
