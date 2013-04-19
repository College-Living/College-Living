package com.collegeliving;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class ImageLoader extends AsyncTask<String, Void, Bitmap> {

	private ImageView image;
	
	public ImageLoader(ImageView image, String url) {
		this.image = image;
		this.execute(url);
	}
	
	protected Bitmap loadBitmap(String url) {
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
	
	@Override
	protected Bitmap doInBackground(String... url) {
		return loadBitmap(url[0]);
	}
	
	@Override
	protected void onPostExecute(Bitmap img) {
		image.setImageBitmap(img);
	}

}
