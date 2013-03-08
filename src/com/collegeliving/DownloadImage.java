package com.collegeliving;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
	private ImageView view;
	
	public DownloadImage(ImageView view) {
		this.view = view;
	}
	
	protected Bitmap doInBackground(String... arg0) {
		String url = arg0[0];
		Bitmap image = null;
		try {
			InputStream in = new java.net.URL(url).openStream();
			image = BitmapFactory.decodeStream(in);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	protected void onPostExecute(Bitmap image) {
		view.setImageBitmap(image);
	}

}
