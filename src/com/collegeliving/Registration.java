package com.collegeliving;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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
		setContentView(R.layout.activity_registration);
		getQuestionnaire();
		setRegisterBtn();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_login_screen, menu);
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
				public void Run(String p) {
					
				}
			};
			new ServerPost(json, registerResponse).execute("/collegeliving/post/register.php");
		}
	}
	
	private boolean validateFormInput() {
		return true;
	}
	
	private JSONObject getFormInput() {
		JSONObject json = new JSONObject();
		try {
			EditText emailText = (EditText) findViewById(R.id.registration_email);
			EditText passwordText = (EditText) findViewById(R.id.registration_pw);
			EditText confirmPasswordText = (EditText) findViewById(R.id.registration_confirm_pw);
			EditText firstNameText = (EditText) findViewById(R.id.registration_fname);
			EditText lastNameText = (EditText) findViewById(R.id.registration_lname);
			EditText displayNameText = (EditText) findViewById(R.id.registration_displayname);
			EditText phoneText = (EditText) findViewById(R.id.registration_phonenum);
			String email = emailText.getText().toString();
			String password = passwordText.getText().toString();
			String confirmPassword = confirmPasswordText.getText().toString();
			if(!password.equals(confirmPassword)) {
				Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_LONG).show();
				return null;
			}
			String firstName = firstNameText.getText().toString();
			String lastName = lastNameText.getText().toString();
			String displayName = displayNameText.getText().toString();
			String phone = phoneText.getText().toString();
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
