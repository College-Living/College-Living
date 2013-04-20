package com.collegeliving;

public class MsgRecord {
	public String content;
	public String date;
	public int from;
	public int to;
	public MsgRecord(String content, String date, int from, int to){
		this.content = content;
		this.date = date;
		this.from = from;
		this.to = to;
	}
}
