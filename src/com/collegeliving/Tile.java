package com.collegeliving;

public class Tile {
	int id;
	String topBar;
	String bottomBar;
	String imageURL;
	public Tile(int id, String topBar, String bottomBar, String imageURL) {
		this.id = id;
		this.topBar = topBar;
		this.bottomBar = bottomBar;
		this.imageURL = imageURL;
	}
	
	public String getTopBar() {
		return this.topBar;
	}
	
	public String getBottomBar() {
		return this.bottomBar;
	}
	
	public String getImage() {
		return this.imageURL;
	}
}
