package com.collegeliving;

public class ApartmentRecord {
	public int aptID;
	public String aptName;
	public String phone;
	public String email;
	public double distance;
	public String website;
	public String aboutApt;
	public String thumbnailURL;
	public String thumbnailCache;
	public String price;
	
	public ApartmentRecord(int aptID, String aptName, String price, double distance, String website, String phone, String email, String aboutApt, String thumbnailURL, String thumbnailCache) {
		this.aptID = aptID;
		this.aptName = aptName;
		this.distance = distance;
		this.website = website;
		this.phone = phone;
		this.email = email;
		this.aboutApt = aboutApt;
		this.thumbnailURL = thumbnailURL;
		this.thumbnailCache = thumbnailCache;
		this.price = price;
	}
}
