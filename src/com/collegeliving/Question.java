package com.collegeliving;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class Question {
	private int qid;
	private String question;
	private ArrayList<QuestionOption> options;
	private Spinner spinner;
	private LinearLayout gui;
	public Question(int qid, String question, ArrayList<QuestionOption> options, Activity c) {
		this.qid = qid;
		this.question = question;
		this.options = options;
		this.gui = createGUI(c);
	}
	
	public int getAnswer() {
		int index = spinner.getSelectedItemPosition();
		QuestionSpinnerAdapter spAdpt = (QuestionSpinnerAdapter) spinner.getAdapter();
		QuestionOption option = spAdpt.getItem(index);
		return option.answer;
	}
	
	public int getQid() {
		return qid;
	}
	
	public String getQuestion() {
		return question;
	}
	
	public Spinner getSpinner() {
		return spinner;
	}

	static public class QuestionOption {
		public String option;
		public int answer;
		public QuestionOption(String option, int value) {
			this.option = option;
			this.answer = value;
		}
	}
	
	public LinearLayout getGUI() {
		return gui;
	}
	
	private LinearLayout createGUI(Activity c) {
		LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout qBlock = (LinearLayout) inflater.inflate(R.layout.question_block, null);
		TextView qText = (TextView) qBlock.findViewById(R.id.question);
		Spinner spinOp = (Spinner) qBlock.findViewById(R.id.options);
		this.spinner = spinOp;
		QuestionSpinnerAdapter adapter = new QuestionSpinnerAdapter(c, android.R.layout.simple_spinner_item, options);
		spinOp.setAdapter(adapter);
		qText.setText(question);
		return qBlock;
	}

}
