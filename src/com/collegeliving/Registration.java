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

import org.json.*;

public class Registration extends Activity {
	private final Registration currentActivity;
	private JSONArray questions;
	public Registration() {
		super();
		currentActivity = this;
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

	public void getQuestionnaire() {
		ServerCallback callback = new ServerCallback() {
			JSONArray questions;
			public void Run(String Response) {
				try {
					questions = new JSONArray(Response);
					LayoutInflater inflater = (LayoutInflater) currentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					LinearLayout view = (LinearLayout) currentActivity.findViewById(R.id.questionBlock);
					for(int i = 0; i < questions.length(); i++) {
						JSONObject question = questions.getJSONObject(i);
						String questionText = question.getString("question");
						int qid = question.getInt("qid");
						ArrayList<QuestionOption> options = parseOptions(question.getJSONArray("options"));
						LinearLayout qBlock = (LinearLayout) inflater.inflate(R.layout.question_block, null);
						TextView qText = (TextView) qBlock.findViewById(R.id.question);
						
						Spinner spinOp = (Spinner) qBlock.findViewById(R.id.options);
						QuestionSpinnerAdapter adapter = new QuestionSpinnerAdapter(currentActivity, android.R.layout.simple_spinner_item, options);
						spinOp.setAdapter(adapter);
						qText.setText(questionText);
						view.addView(qBlock);
					}
				} catch (JSONException e) {
					
				}
			}
		};
		new ServerPost(null, callback).execute("/collegeliving/get/questionnaire.php");
	
	}
	
	public ArrayList<QuestionOption> parseOptions(JSONArray opts) {
		ArrayList<QuestionOption> options = new ArrayList<QuestionOption>();
		for(int i = 0; i < opts.length(); i++) {
			try {
				JSONObject o = opts.getJSONObject(i);
				String option = o.getString("option");
				int value = o.getInt("value");
				options.add(new QuestionOption(option, value));
			} catch(JSONException e) {
			
			}
		}
		return options;
	}
	
	public void setRegisterBtn() {
		Button registerBtn = (Button) findViewById(R.id.submitRegisterBtn);
		registerBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				JSONObject json = getFormInput();
				ServerCallback callback = new ServerCallback() {
					public void Run(String p) {
						Log.d("register success", p);
						
					}
					
				};
				new ServerPost(json, callback).execute("/collegeliving/post/register.php");
				
			}
		});
	}
	
	public JSONObject getFormInput() {
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
			String firstName = firstNameText.getText().toString();
			String lastName = lastNameText.getText().toString();
			String displayName = displayNameText.getText().toString();
			String phone = phoneText.getText().toString();
			LinearLayout questions = (LinearLayout) findViewById(R.id.questionBlock);
			json.put("FirstName", firstName);
			json.put("LastName", lastName);
			json.put("Email", email);
			json.put("DisplayName", displayName);
			json.put("Password", password);
			json.put("Phone", phone);
			JSONArray responses = new JSONArray();
			for(int i = 0; i < questions.getChildCount(); i++) {
				JSONObject response = new JSONObject();
				int qid = i+1;
				LinearLayout question = (LinearLayout) questions.getChildAt(i);
				Spinner answer = (Spinner) question.findViewById(R.id.options);
				QuestionSpinnerAdapter adapter = (QuestionSpinnerAdapter) answer.getAdapter();
				int index = answer.getSelectedItemPosition();
				QuestionOption selected = adapter.getItem(index);
				int ans = selected.value;
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
