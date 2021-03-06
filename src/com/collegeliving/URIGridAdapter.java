package com.collegeliving;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class URIGridAdapter extends BaseAdapter {

	private ArrayList<Tile> tiles;
	private LayoutInflater inflater;
	private Context context;
	private ConcurrentHashMap<Integer, ImageThumb> store;
	public URIGridAdapter(Context c, ArrayList<Tile> tiles) {
		this.context = c;
		this.inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.store = new ConcurrentHashMap<Integer, ImageThumb>();
		this.tiles = tiles;
		for(int i = 0; i < tiles.size(); i++) {
			store.put(tiles.get(i).id, new ImageThumb(tiles.get(i).id, tiles.get(i).getImage()));
		}
	}
	
	@Override
	public int getCount() {
		return tiles.size();
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
		Tile tile = tiles.get(position);
		ImageThumb img = store.get(tile.id);
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
			secondary_info.setText(Html.fromHtml(tiles.get(position).getSecondaryInfo()));
		}

		return frame;
	}
	
	private class ImageThumb {
		private String url = null;
		private Bitmap image = null;
		private int id = 0;
		private DownloadImageTask download;
		public int keep = 0;
		
		
		public ImageThumb(int id, String url) {
			this.setId(id);
			this.setUrl(url);
			download = new DownloadImageTask();
			download.execute(this);
		}

		public void setId(int id) {
			this.id = id;
		}
		
		public AsyncTask.Status getStatus() {
			return download.getStatus();
		}
		
		public int getId() {
			return id;
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
	
	public void updateData(ArrayList<Tile> tiles) {
		this.tiles = tiles;
		for(int i = 0; i < tiles.size(); i++) {
			Tile tile = tiles.get(i);
			if(!store.containsKey(tile.id)) {
				store.put(tile.id, new ImageThumb(tile.id, tile.imageURL));
			}
			store.get(tile.id).keep = 1;
		}
		//for(Map.Entry<Integer, ImageThumb> entry : store.entrySet()) {
		//	if(entry.getValue().keep != 1)
		//		store.remove(entry.getKey());
		//	else
		//		entry.getValue().keep = 0;
		//}
		notifyUpdate();
	}
	
	private class DownloadImageTask extends AsyncTask<ImageThumb, Void, Void> {

		private Bitmap loadImage(String url) {
			Bitmap image = null;
			try {
				int TILE_SIZE = 100;
				InputStream tmp_stream = new java.net.URL(url).openStream();
				BitmapFactory.Options tmp = new BitmapFactory.Options();
				tmp.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(tmp_stream, null, tmp);
				int width = tmp.outWidth; int height = tmp.outHeight;
				int scale = 1;
				while(width/scale/2 >= TILE_SIZE && height/scale/2 >= TILE_SIZE)
					scale *= 2;
				BitmapFactory.Options o = new BitmapFactory.Options();
				o.inSampleSize = scale;
				o.inPurgeable = true;
				InputStream in = new java.net.URL(url).openStream();
				image = BitmapFactory.decodeStream(in, null, o);
				
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
			ImageThumb image = images[0];
			image.setImage(loadImage("http://"+ServerPost.server_ip+"/collegeliving/"+image.url));
			publishProgress();
			return null;
		}
	}
		
}
