package com.collegeliving;

public class Tile {
	int id;
	String topBar;
	String primaryInfo;
	String secondaryInfo;
	String imageURL;
	public Tile(int id, String topBar, String primaryInfo, String secondaryInfo, String imageURL) {
		this.id = id;
		this.topBar = topBar;
		this.primaryInfo = primaryInfo;
		this.secondaryInfo = secondaryInfo;
		this.imageURL = imageURL;
	}
	
	public String getTopBar() {
		return this.topBar;
	}
	
	public String getPrimaryInfo() {
		return this.primaryInfo;
	}
	public String getSecondaryInfo() {
		return this.secondaryInfo;
	}
	
	public String getImage() {
		return this.imageURL;
	}
}
