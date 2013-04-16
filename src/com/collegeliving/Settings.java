package com.collegeliving;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
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
		setRadiusOnchange();
		setDisplayNameOnchange();
		setPasswordOnchange();
	}
	
	public void setEmailOnchange(){
		EditText email_setting = (EditText) findViewById(R.id.Email_setting);
		email_setting.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            	EditText email = (EditText) v;
        		String oldemail = new String();
            	if(!hasFocus){
                	String newemail = email.getText().toString();
                	if(!oldemail.equals(newemail)){
                		Toast.makeText(getApplicationContext(), "email changed", Toast.LENGTH_SHORT).show();
                	}
                }else{
                	oldemail = email.getText().toString();
                }
            }
        });
	}
	public void setRadiusOnchange(){
		EditText radius_setting = (EditText) findViewById(R.id.Radius_setting);
		radius_setting.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            	EditText radius = (EditText) v;
            	String oldradius = new String();
                if(!hasFocus){
                	String newradius = radius.getText().toString();
                	if(!oldradius.equals(newradius))
                		Toast.makeText(getApplicationContext(), "radius changed", Toast.LENGTH_SHORT).show();
                }else{
                	oldradius = radius.getText().toString();
                }
            }
        });
	}
	public void setDisplayNameOnchange(){
		EditText displayname_setting = (EditText) findViewById(R.id.DisplayName_setting);
		displayname_setting.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            	EditText displayname = (EditText) v;
            	String olddisplayname = new String();
                if(!hasFocus){
                    String newdisplayname = displayname.getText().toString();
                    if(!olddisplayname.equals(newdisplayname))
                    	Toast.makeText(getApplicationContext(), "displayname changed", Toast.LENGTH_SHORT).show();
                }else{
                	olddisplayname = displayname.getText().toString();
                }
            }
        });
	}
	public void setPasswordOnchange(){
		EditText password_setting = (EditText) findViewById(R.id.Password_setting);
		password_setting.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            	EditText pw = (EditText) v;
            	String oldpw = new String();
                if(!hasFocus){
                    String newpw = pw.getText().toString();
                    if(!oldpw.equals(newpw))
                    	Toast.makeText(getApplicationContext(), "password changed", Toast.LENGTH_SHORT).show();
                }else{
                	oldpw = pw.getText().toString();
                }
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
