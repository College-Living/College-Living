package com.collegeliving;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class QuestionSpinnerAdapter extends ArrayAdapter<Question.QuestionOption>{

    // Your sent context
    private Context context;
    // Your custom values for the spinner (User)
    private ArrayList<Question.QuestionOption> options;

    public QuestionSpinnerAdapter(Context context, int textViewResourceId,
            ArrayList<Question.QuestionOption> options) {
        super(context, textViewResourceId);
        this.context = context;
        this.options = options;
    }

    public int getCount(){
       return options.size();
    }

    public Question.QuestionOption getItem(int position){
       return options.get(position);
    }

    public long getItemId(int position){
       return position;
    }


    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = new TextView(context);
        label.setTextColor(Color.WHITE);
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        Question.QuestionOption opt = options.get(position);
        label.setText(opt.option);

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
            ViewGroup parent) {
    	TextView label = new TextView(context);
        label.setTextColor(Color.WHITE);
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        Question.QuestionOption opt = options.get(position);
		label.setText(opt.option);
        
        return label;
    }
}