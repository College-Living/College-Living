package com.collegeliving;

public class RoomieRecord {
	public int uID;
	public String displayName;
	public String phone;
	public String email;
	public double compatScore;
	public String aboutMe;
	public String thumbnailURL;
	public String thumbnailCache;
	
	public RoomieRecord(int uID, String displayName, String phone, String email, String aboutMe, String thumbnailURL, double compatScore) {
		this.uID = uID;
		this.displayName = displayName;
		this.phone = phone;
		this.email = email;
		this.aboutMe = aboutMe;
		this.thumbnailURL = thumbnailURL;
		this.compatScore = compatScore;
	}
}
