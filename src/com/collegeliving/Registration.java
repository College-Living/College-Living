package com.collegeliving;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.*;

public class Registration extends Activity {
	private ArrayList<Question> questions;
	public Registration() {
		super();
		questions = new ArrayList<Question>();
	}
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_registration);
		getQuestionnaire();
		setRegisterBtn();
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
	
	private void getQuestionnaire() {
		ServerCallback callback = new ServerCallback() {
			JSONArray questions;
			public void Run(String Response) {
				try {
					questions = new JSONArray(Response);
					for(int i = 0; i < questions.length(); i++) {
						JSONObject question = questions.getJSONObject(i);
						String questionText = question.getString("question");
						int qid = question.getInt("qid");
						ArrayList<Question.QuestionOption> options = parseOptions(question.getJSONArray("options"));
						addQuestion(questionText, qid, options);
					}
				} catch (JSONException e) {
					
				}
			}
		};
		new ServerPost(null, callback).execute("/collegeliving/get/questionnaire.php");
	
	}
	
	private void addQuestion(String qText, int qid, ArrayList<Question.QuestionOption> options) {
		LinearLayout view = (LinearLayout) findViewById(R.id.questionBlock);
		Question q = new Question(qid, qText, options, this);
		questions.add(q);
		view.addView(q.getGUI());
	}
	
	private ArrayList<Question.QuestionOption> parseOptions(JSONArray opts) {
		ArrayList<Question.QuestionOption> options = new ArrayList<Question.QuestionOption>();
		for(int i = 0; i < opts.length(); i++) {
			try {
				JSONObject o = opts.getJSONObject(i);
				String option = o.getString("option");
				int value = o.getInt("value");
				options.add(new Question.QuestionOption(option, value));
			} catch(JSONException e) {
			
			}
		}
		return options;
	}
	
	private void setRegisterBtn() {
		Button registerBtn = (Button) findViewById(R.id.submitRegisterBtn);
		registerBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				registerUser();
			}
		});
	}
	
	private void registerUser() {
		if(validateFormInput()) {
			JSONObject json = getFormInput();
			ServerCallback registerResponse = new ServerCallback() {
				public void Run(String response) {
					JSONObject json;
					try {
						json = new JSONObject(response);
						boolean success = json.getBoolean("success");
						String message = json.getString("message");
						if(success) { 
							registerSuccess();
						} else
							showRegisterError(message);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			new ServerPost(json, registerResponse).execute("/collegeliving/post/register.php");
		}
	}
	
	private void registerSuccess() {
		this.finish();
	}
	
	private void showRegisterError(String error) {
		Toast.makeText(this, error, Toast.LENGTH_LONG).show();
	}
	
	private boolean validateFormInput() {
		String email = getEmail();
		String password = getPassword();
		String confirmPassword = getConfirmPassword();
		String firstName = getFirstName();
		String lastName = getLastName();
		String displayName = getDisplayName();
		String phone = getPhoneNumber();
		String emailPattern = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		
		if(!email.matches(emailPattern)) {
			Toast.makeText(this, "Invalid e-mail address", Toast.LENGTH_LONG).show();
			return false;
		} else if(password.isEmpty()) {
			Toast.makeText(this, "Please enter a password", Toast.LENGTH_LONG).show();
			return false;
		} else if(!password.equals(confirmPassword)) {
			Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show();
			return false;
		} else if(firstName.isEmpty() || lastName.isEmpty()) {
			Toast.makeText(this, "Please enter your name", Toast.LENGTH_LONG).show();
			return false;
		} else if(displayName.isEmpty()) {
			Toast.makeText(this, "Please enter a display name", Toast.LENGTH_LONG).show();
			return false;
		} else if(phone.isEmpty()) {
			Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}
	
	private String getEmail() {
		EditText emailText = (EditText) findViewById(R.id.registration_email);
		return emailText.getText().toString();
	}
	
	private String getFirstName() {
		EditText firstNameText = (EditText) findViewById(R.id.registration_fname);
		return firstNameText.getText().toString();
	}
	
	private String getLastName() {
		EditText lastNameText = (EditText) findViewById(R.id.registration_lname);
		return lastNameText.getText().toString();
	}
	
	private String getDisplayName() {
		EditText displayNameText = (EditText) findViewById(R.id.registration_displayname);
		return displayNameText.getText().toString();
	}
	
	private String getPhoneNumber() {
		EditText phoneText = (EditText) findViewById(R.id.registration_phonenum);
		return phoneText.getText().toString();
	}
	
	private String getPassword() {
		EditText passwordText = (EditText) findViewById(R.id.registration_pw);
		return passwordText.getText().toString();
		
	}
	
	private String getConfirmPassword() {
		EditText confirmPasswordText = (EditText) findViewById(R.id.registration_confirm_pw);
		return confirmPasswordText.getText().toString();
	}
	
	private JSONObject getFormInput() {
		JSONObject json = new JSONObject();
		try {
			String email = getEmail();
			String password = getPassword();
			String confirmPassword = getConfirmPassword();
			String firstName = getFirstName();
			String lastName = getLastName();
			String displayName = getDisplayName();
			String phone = getPhoneNumber();
			json.put("FirstName", firstName);
			json.put("LastName", lastName);
			json.put("Email", email);
			json.put("DisplayName", displayName);
			json.put("Password", password);
			json.put("Phone", phone);
			JSONArray responses = new JSONArray();
			for(int i = 0; i < questions.size(); i++) {
				Question q = questions.get(i);
				int qid = q.getQid();
				int ans = q.getAnswer();
				JSONObject response = new JSONObject();
				response.put("qid", qid);
				response.put("value", ans);
				responses.put(response);
				
	 		}
			json.put("answers", responses);
		} catch(JSONException e) {
			
		}
		return json;
	}
}
