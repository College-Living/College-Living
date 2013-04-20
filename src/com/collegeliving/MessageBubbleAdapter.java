package com.collegeliving;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MsgRecord msg = messages.get(position);
		Log.i("msg", msg.content);
		int uid = context.getLoggedInUser();
		int resource = 0;
		if(msg.from==uid)
			resource = R.layout.to_message_bubble;
		else
			resource = R.layout.from_message_bubble;
		LinearLayout message_block;
		if(convertView != null) message_block = (LinearLayout) convertView;
		else message_block = (LinearLayout) this.inflater.inflate(resource, parent, false);
		TextView msg_tv = (TextView) message_block.findViewById(R.id.msg_bubble);
		msg_tv.setText(msg.content);
		return message_block;
	}
	
	
	protected void notifyUpdate() {
		this.notifyDataSetChanged();
	}

}
