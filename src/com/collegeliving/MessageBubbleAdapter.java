package com.collegeliving;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MessageBubbleAdapter extends BaseAdapter {

	private ArrayList<MsgRecord> messages;
	private LayoutInflater inflater;
	private LocationActivity context;
	
	public MessageBubbleAdapter(LocationActivity c, ArrayList<MsgRecord> msgs) {
		this.context = c;
		this.inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.messages = msgs;
	}
	
	@Override
	public int getCount() {
		return messages.size();
	}

	@Override
	public MsgRecord getItem(int position) {
		return messages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void updateData(ArrayList<MsgRecord> msgs) {
		this.messages = msgs;
		this.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MsgRecord msg = messages.get(position);
		int uid = context.getLoggedInUser();
		int resource = 0;
		
		if(msg.from==uid) {
			resource = R.layout.to_message_bubble;
		}
		else
			resource = R.layout.from_message_bubble;
		
		SimpleDateFormat db_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		Date timestamp = new Date();
		Date timestamp_prev = new Date();
		long time_diff = 0;
		try {
			timestamp = db_format.parse(msg.date);
			if(position > 0) {
				timestamp_prev = db_format.parse(messages.get(position-1).date);
				time_diff = (timestamp.getTime() - timestamp_prev.getTime())/1000/60;
				Log.i("timediff", "timediff between "+timestamp.toString()+" AND "+timestamp_prev.toString()+" is "+time_diff);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		SimpleDateFormat pub_format = new SimpleDateFormat("MMMM dd hh:mm a", Locale.US);
		String pub_timestamp = pub_format.format(timestamp);
		
		RelativeLayout message_block;
		message_block = (RelativeLayout) this.inflater.inflate(resource, parent, false);
		TextView tv_msg = (TextView) message_block.findViewById(R.id.msg_bubble);
		TextView tv_timestamp = (TextView) message_block.findViewById(R.id.timestamp);
	
		tv_msg.setText(msg.content);
		if(time_diff <= 10 && position > 0)
			tv_timestamp.setVisibility(View.INVISIBLE);
		else
			tv_timestamp.setText(pub_timestamp);
		return message_block;
	}
	
	
	protected void notifyUpdate() {
		this.notifyDataSetChanged();
	}

}
