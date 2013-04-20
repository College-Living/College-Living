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
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class URIGridAdapter extends BaseAdapter {

	private ImageThumb images[];
	private ArrayList<Tile> tiles;
	private LayoutInflater inflater;
	private DownloadImagesTask downloadTask;
	private Context context;
	
	public URIGridAdapter(Context c, ArrayList<Tile> tiles) {
		this.context = c;
		this.inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.images = new ImageThumb[tiles.size()];
		this.tiles = tiles;
		for(int i = 0; i < tiles.size(); i++) {
			images[i] = new ImageThumb(tiles.get(i).getImage(), null);
		}
		downloadTask = new DownloadImagesTask();
		downloadTask.execute(images);
	}
	
	@Override
	public int getCount() {
		return images.length;
	}

	@Override
	public Tile getItem(int position) {
		return this.tiles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FrameLayout frame;
		if(convertView != null) frame = (FrameLayout) convertView;
		else frame = (FrameLayout) this.inflater.inflate(R.layout.tile, parent, false);
				
		ImageView view = (ImageView) frame.findViewById(R.id.thumbnail);
		TextView title = (TextView) frame.findViewById(R.id.info_title);
		TextView primary_info = (TextView) frame.findViewById(R.id.primary_info);
		TextView secondary_info = (TextView) frame.findViewById(R.id.secondary_info);
		
		ImageThumb img = images[position];
		if(img.image != null) {
			view.setImageBitmap(img.image);
		} else {
			view.setImageDrawable(context.getResources().getDrawable(R.drawable.img_placeholder));
		}
		
		String top_bar_text = tiles.get(position).getTopBar(); 
		String primary_text = tiles.get(position).getPrimaryInfo();
		String secondary_text = tiles.get(position).getSecondaryInfo();
		title.setText(top_bar_text);
		primary_info.setText(primary_text);
		if(secondary_text.equals(""))
			secondary_info.setVisibility(View.GONE);
		else {
			secondary_info.setVisibility(View.VISIBLE);
			secondary_info.setText(tiles.get(position).getSecondaryInfo());
		}

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
				img.setImage(loadImage("http://"+ServerPost.server_ip+"/collegeliving/"+img.url));
				publishProgress();
			}
			return null;
		}
				
		
	}

}
