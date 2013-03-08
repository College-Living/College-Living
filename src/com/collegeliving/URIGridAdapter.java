package com.collegeliving;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class URIGridAdapter extends BaseAdapter {

	private ImageThumb images[];
	private LayoutInflater inflater;
	private DownloadImagesTask downloadTask;
	
	public URIGridAdapter(Context c, ArrayList<String> urls) {
		this.inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.images = new ImageThumb[urls.size()];
		for(int i = 0; i < urls.size(); i++) {
			images[i] = new ImageThumb(urls.get(i), null);
		}
		downloadTask = new DownloadImagesTask();
		downloadTask.execute(images);
	}
	
	@Override
	public int getCount() {
		return images.length;
	}

	@Override
	public Object getItem(int position) {
		return this.images[position].url;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FrameLayout frame;
		if(convertView != null) frame = (FrameLayout) convertView;
		else frame = (FrameLayout) this.inflater.inflate(R.layout.tile, null);
		
		frame.setLayoutParams(new GridView.LayoutParams(40, 40));
		
		ImageView view = (ImageView) frame.findViewById(R.id.thumbnail);
		TextView text = (TextView) frame.findViewById(R.id.info_caption);
		
		ImageThumb img = images[position];
		if(img.image != null) {
			view.setImageBitmap(img.image);
		} else {
			view.setImageResource(R.drawable.ic_launcher);
		}
		
		text.setText("Test "+Integer.toString(position));

		return frame;
	}
	
	private class ImageThumb {
		private String url = null;
		private Bitmap image = null;
		
		public ImageThumb(String url, Bitmap image) {
			this.setUrl(url);
			this.setImage(image);
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public Bitmap getImage() {
			return image;
		}

		public void setImage(Bitmap image) {
			this.image = image;
		}
		
	}
	
	protected void notifyUpdate() {
		Log.d("Finished", "Done loading image");
		this.notifyDataSetChanged();
	}
	
	private class DownloadImagesTask extends AsyncTask<ImageThumb, Void, Void> {

		private Bitmap loadImage(String url) {
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
		protected void onProgressUpdate(Void... param) {
			notifyUpdate();
		}
		
		@Override
		protected Void doInBackground(ImageThumb... images) {
			for(int i = 0; i < images.length; i++) {
				ImageThumb img = images[i];
				if(img.getImage() != null) continue;
				img.setImage(loadImage(img.url));
				publishProgress();
			}
			return null;
		}
				
		
	}

}
