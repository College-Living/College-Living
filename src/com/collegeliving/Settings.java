package com.collegeliving;

import org.json.JSONException;
import org.json.JSONObject;

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
		load_setting();
		setLogoutBtn();
		setEmailOnchange();
		setRadiusOnchange();
		setDisplayNameOnchange();
		setPasswordOnchange();
	}
	
	private void update_setting(String field, String value){
			SharedPreferences preferences = getSharedPreferences("UserSharedPreferences", 0);
			int user_id = preferences.getInt("UID", -1);
    		JSONObject json = new JSONObject();
    		try {
    			json.put("method", "set");
				json.put("uid", user_id);
				json.put("field", field);
				json.put("value", value);
			} catch (JSONException e) {
				e.printStackTrace();
			}
    		ServerCallback settingResponse = new ServerCallback(){
				@Override
				public void Run(String response) {
					try {
						JSONObject json = new JSONObject(response);
						boolean success = json.getBoolean("success");
						if(success){
	                		Toast.makeText(getApplicationContext(), "Info updated", Toast.LENGTH_SHORT).show();
						}else{
	                		Toast.makeText(getApplicationContext(), "um?", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}                		
    		};
    		new ServerPost(json, settingResponse).execute("/collegeliving/post/setting.php");
	}
	
	private void load_setting(){
		SharedPreferences preferences = getSharedPreferences("UserSharedPreferences", 0);
		int user_id = preferences.getInt("UID", -1);
		JSONObject json = new JSONObject();
		try {
			json.put("method", "load");
			json.put("uid", user_id);
		} catch (JSONException e){
			e.printStackTrace();
		}
		ServerCallback settingResponse = new ServerCallback(){
			@Override
			public void Run(String response) {
				try {
					JSONObject json = new JSONObject(response);
					boolean success = json.getBoolean("success");
					if(success){
						String email = json.getString("email");
						String displayname = json.getString("displayname");
						String radius = json.getString("radius");
						EditText email_setting = (EditText) findViewById(R.id.Email_setting);
						email_setting.setText(email);
						EditText radius_setting = (EditText) findViewById(R.id.Radius_setting);
						radius_setting.setText(radius);
						EditText displayname_setting = (EditText) findViewById(R.id.DisplayName_setting);
						displayname_setting.setText(displayname);
					}else{
						Toast.makeText(getApplicationContext(), "Can't connect to the server", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}				
			}
		};
		new ServerPost(json, settingResponse).execute("/collegeliving/post/setting.php");
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
                		update_setting("Email", newemail);
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
                		update_setting("Radius", newradius);
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
                		update_setting("DisplayName", newdisplayname);
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
                		update_setting("Password", newpw);
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
