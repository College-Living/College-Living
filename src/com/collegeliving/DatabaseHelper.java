package com.collegeliving;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static DatabaseHelper mInstance = null;
	static String DB_NAME = "CollegeLivingDB";
	static int DB_VERSION = 6;
	
	static public class Roomie {
		static String TABLE = "Roomie";
		static String ID = "UID";
		static String DISPLAY_NAME = "DisplayName";
		static String EMAIL = "Email";
		static String PHONE = "Phone";
		static String COMPATIBILITY = "Compatibility";
		static String ABOUT_ME = "AboutMe";
		static String THUMBNAIL_CACHE = "ThumbnailCache";
		static String THUMBNAIL_URL = "ThumbnailURL";
	}
	
	static public class Pad {
		static String TABLE = "Pad";
		static String ID = "AID";
		static String DISPLAY_NAME = "ApartmentName";
		static String WEBSITE = "Website";
		static String EMAIL = "Email";
		static String PHONE = "Phone";
		static String PRICE_RANGE = "PriceRange";
		static String ABOUT_APT = "AboutApt";
		static String LONGITUDE = "Longitude";
		static String LATITUDE = "Latitude";
		static String DISTANCE = "Distance";
		static String THUMBNAIL_CACHE = "ThumbnailCache";
		static String THUMBNAIL_URL = "ThumbnailURL";
	}
	
	static public class Message {
		static String TABLE = "Message";
		static String ID = "MsgID";
		static String SENT_FROM = "FromUID";
		static String SEND_TO = "ToUID";
		static String DATE = "Timestamp";
		static String CONTENT = "Content";
	}
	
	static public DatabaseHelper getInstance(Context ctx) {
		if(mInstance == null)
			mInstance = new DatabaseHelper(ctx.getApplicationContext());
		return mInstance;
	}
	
	private DatabaseHelper(Context ctx) {
		super(ctx, DB_NAME, null, DB_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String roomie_sql = "CREATE TABLE IF NOT EXISTS "+Roomie.TABLE + " (" +
							 Roomie.ID+" INTEGER PRIMARY KEY, " +
							 Roomie.DISPLAY_NAME+ " VARCHAR(100), " +
							 Roomie.EMAIL+ " TEXT, " +
							 Roomie.PHONE+ " VARCHAR(50), " +
							 Roomie.ABOUT_ME + " TEXT, " +
							 Roomie.COMPATIBILITY + " FLOAT, " +
							 Roomie.THUMBNAIL_URL + " TEXT, " +
							 Roomie.THUMBNAIL_CACHE + " TEXT" +
							 ");";
		db.execSQL(roomie_sql);
		String pad_sql = "CREATE TABLE IF NOT EXISTS "+Pad.TABLE + " (" +
						 Pad.ID+" INTEGER PRIMARY KEY, " +
						 Pad.DISPLAY_NAME+ " VARCHAR(100), " +
						 Pad.EMAIL+ " TEXT, " +
						 Pad.PHONE+ " VARCHAR(50), " +
						 Pad.WEBSITE+ " TEXT, " +
						 Pad.ABOUT_APT + " TEXT, " +
						 Pad.THUMBNAIL_URL + " TEXT, " +
						 Pad.THUMBNAIL_CACHE + " TEXT, " +
						 Pad.LONGITUDE + " DECIMAL, " +
						 Pad.LATITUDE + " DECIMAL, " +
						 Pad.DISTANCE + " DECIMAL, " +
						 Pad.PRICE_RANGE + " TEXT" +
						 ");";
		db.execSQL(pad_sql);
		String msg_sql = "CREATE TABLE IF NOT EXISTS "+Message.TABLE + " (" +
				 Message.ID+" INTEGER AUTO INCREMENT PRIMARY KEY, " +
				 Message.CONTENT+ " VARCHAR(1000), " +
				 Message.SENT_FROM+ " INTEGER, " +
				 Message.SEND_TO+ " INTEGER, " +
				 Message.DATE + " DATETIME " +
				 ");";
		db.execSQL(msg_sql);
	}
	
	public long insertMessage(String content, int from, int to, String date) {
		long rowID = -1;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(Message.SENT_FROM, from);
		cv.put(Message.SEND_TO, to);
		cv.put(Message.DATE, date);
		cv.put(Message.CONTENT, content);
		rowID = db.insert(Message.TABLE, null, cv);
		return rowID;
	}
	
	public ArrayList<MsgRecord> getConversation(int user1, int user2) {
		SQLiteDatabase db = this.getReadableDatabase();
		String uid1 = String.valueOf(user1);
		String uid2 = String.valueOf(user2);
		Cursor cursor = db.query(Message.TABLE, null, "("+Message.SENT_FROM+"=? AND "+Message.SEND_TO+"= ?) OR ("+Message.SENT_FROM+"=? AND "+Message.SEND_TO+")", new String[] {uid1, uid2, uid2, uid1}, null, null, Message.DATE);
		ArrayList<MsgRecord> messages = new ArrayList<MsgRecord>();
		if(cursor.moveToFirst()) {
			do {
			String content = cursor.getString(cursor.getColumnIndex(Message.CONTENT));
			String date = cursor.getString(cursor.getColumnIndex(Message.DATE));
			int from = cursor.getInt(cursor.getColumnIndex(Message.SENT_FROM));
			int to = cursor.getInt(cursor.getColumnIndex(Message.SEND_TO));;
			messages.add(new MsgRecord(content, date, from, to));
			} while(cursor.moveToNext());
		}
		return messages;
	}
	
	public long insertOrUpdatePad(int aptID, String aptName, String price, double distance, double lon, double lat, String website, String phone, String email, String aboutApt, String thumbnailURL) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		long rowID = -1;
		cv.put(Pad.DISPLAY_NAME, aptName);
		cv.put(Pad.LONGITUDE, lon);
		cv.put(Pad.LATITUDE, lat);
		cv.put(Pad.DISTANCE, distance);
		cv.put(Pad.WEBSITE, website);
		cv.put(Pad.PHONE, phone);
		cv.put(Pad.EMAIL, email);
		cv.put(Pad.ABOUT_APT, aboutApt);
		cv.put(Pad.THUMBNAIL_URL, thumbnailURL);
		cv.put(Pad.PRICE_RANGE, price);
		if(!padExists(aptID)) {
			cv.put(Pad.ID, aptID);
			rowID = db.insert(Pad.TABLE, null, cv);
		} else {
			rowID = db.update(Pad.TABLE, cv, Pad.ID+" = ?", new String[] {String.valueOf(aptID)});
		}
		return rowID;
	}
	
	private boolean padExists(int aptID) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(Pad.TABLE, null, Pad.ID+" = ?", new String[] {String.valueOf(aptID)}, null, null, null);
		int count = cursor.getCount();
		if(cursor != null && !cursor.isClosed())
			cursor.close();
		if(count > 0) return true;
		else return false;
	}
	
	public ApartmentRecord getPad(int aptID) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(Pad.TABLE, null, Pad.ID + " = ?", new String[] {String.valueOf(aptID)}, null, null, null);
		ApartmentRecord apartment = null;
		if(cursor.moveToFirst()) {
			String aptName = cursor.getString(cursor.getColumnIndex(Pad.DISPLAY_NAME));
			double distance = cursor.getDouble(cursor.getColumnIndex(Pad.DISTANCE));
			double lon = cursor.getDouble(cursor.getColumnIndex(Pad.LONGITUDE));
			double lat = cursor.getDouble(cursor.getColumnIndex(Pad.LATITUDE));
			String website = cursor.getString(cursor.getColumnIndex(Pad.WEBSITE));
			String phone = cursor.getString(cursor.getColumnIndex(Pad.PHONE));
			String email = cursor.getString(cursor.getColumnIndex(Pad.EMAIL));
			String aboutApt = cursor.getString(cursor.getColumnIndex(Pad.ABOUT_APT));
			String thumbnailURL = cursor.getString(cursor.getColumnIndex(Pad.THUMBNAIL_URL));
			String thumbnailCache = cursor.getString(cursor.getColumnIndex(Pad.THUMBNAIL_CACHE));
			String price = cursor.getString(cursor.getColumnIndex(Pad.PRICE_RANGE));
			apartment = new ApartmentRecord(aptID, aptName, price, distance, website, phone, email, aboutApt, thumbnailURL, thumbnailCache);
		}
		return apartment;
	}
	
	public ArrayList<ApartmentRecord> getPads() {
		ArrayList<ApartmentRecord> pads = new ArrayList<ApartmentRecord>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(Pad.TABLE, null, null, null, null, null, Pad.DISTANCE);
		if(cursor.moveToFirst()) {
			do {
				int aptID = cursor.getInt(cursor.getColumnIndex(Pad.ID));
				String aptName = cursor.getString(cursor.getColumnIndex(Pad.DISPLAY_NAME));
				double distance = cursor.getDouble(cursor.getColumnIndex(Pad.DISTANCE));
				String website = cursor.getString(cursor.getColumnIndex(Pad.WEBSITE));
				String phone = cursor.getString(cursor.getColumnIndex(Pad.PHONE));
				String email = cursor.getString(cursor.getColumnIndex(Pad.EMAIL));
				String aboutApt = cursor.getString(cursor.getColumnIndex(Pad.ABOUT_APT));
				String thumbnailURL = cursor.getString(cursor.getColumnIndex(Pad.THUMBNAIL_URL));
				String thumbnailCache = cursor.getString(cursor.getColumnIndex(Pad.THUMBNAIL_CACHE));
				String price = cursor.getString(cursor.getColumnIndex(Pad.PRICE_RANGE));
				pads.add(new ApartmentRecord(aptID, aptName, price, distance, website, phone, email, aboutApt, thumbnailURL, thumbnailCache));
			} while(cursor.moveToNext());
		}
		if(cursor != null && !cursor.isClosed())
			cursor.close();
		return pads;
	}
	
	public long insertOrUpdateRoomie(int uID, String displayName, String phone, String email, String aboutMe, String thumbnailURL, double compatScore) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		long rowID = -1;
		cv.put(Roomie.DISPLAY_NAME, displayName);
		cv.put(Roomie.PHONE, phone);
		cv.put(Roomie.EMAIL, email);
		cv.put(Roomie.ABOUT_ME, aboutMe);
		cv.put(Roomie.THUMBNAIL_URL, thumbnailURL);
		cv.put(Roomie.COMPATIBILITY, compatScore);
		if(!roomieExists(uID)) {
			cv.put(Roomie.ID, uID);
			rowID = db.insert(Roomie.TABLE, null, cv);
		} else {
			rowID = db.update(Roomie.TABLE, cv, Roomie.ID+" = ?", new String[] {String.valueOf(uID)});
		}
		return rowID;
	}
	
	private boolean roomieExists(int uID) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(Roomie.TABLE, null, Roomie.ID+" = ?", new String[] {String.valueOf(uID)}, null, null, null);
		int count = cursor.getCount();
		if(cursor != null && !cursor.isClosed())
			cursor.close();
		if(count > 0) return true;
		else return false;
	}
	
	public ArrayList<RoomieRecord> getRoomies() {
		ArrayList<RoomieRecord> roomies = new ArrayList<RoomieRecord>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(Roomie.TABLE, null, null, null, null, null, Roomie.COMPATIBILITY+" DESC");
		if(cursor.moveToFirst()) {
			do {
				int uid = cursor.getInt(cursor.getColumnIndex(Roomie.ID));
				String displayName = cursor.getString(cursor.getColumnIndex(Roomie.DISPLAY_NAME));
				String email = cursor.getString(cursor.getColumnIndex(Roomie.EMAIL));
				String phone = cursor.getString(cursor.getColumnIndex(Roomie.PHONE));
				String aboutMe = cursor.getString(cursor.getColumnIndex(Roomie.ABOUT_ME));
				double compatScore = cursor.getDouble(cursor.getColumnIndex(Roomie.COMPATIBILITY));
				String thumbnail = cursor.getString(cursor.getColumnIndex(Roomie.THUMBNAIL_URL));
				roomies.add(new RoomieRecord(uid, displayName, phone, email, aboutMe, thumbnail, compatScore));
			} while(cursor.moveToNext());
		}
		return roomies;
	}
	
	public void clearRoomies() {
		SQLiteDatabase db = this.getWritableDatabase();
		dropRoomieTable(db);
		onCreate(db);
	}
	
	public void clearPads() {
		SQLiteDatabase db = this.getWritableDatabase();
		dropPadTable(db);
		onCreate(db);
	}
	
	public void dropRoomieTable(SQLiteDatabase db) {
		db.execSQL("DROP TABLE "+Roomie.TABLE+";");
	}
	
	public void dropPadTable(SQLiteDatabase db) {
		db.execSQL("DROP TABLE "+Pad.TABLE+";");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		dropRoomieTable(db);
		dropPadTable(db);
		onCreate(db);
	}

}
