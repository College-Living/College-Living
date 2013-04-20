package com.collegeliving;

import android.os.Bundle;

public class ChatScreen extends LocationActivity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_screen);
		setSyncService();
		
	}
	
	private void setSyncService(){
		while(true){
			try {
				Thread.sleep(1000);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}
	}
}
