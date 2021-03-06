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
	static int DB_VERSION = 1;
	
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
		static String MSG_COUNT = "MsgCount";
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
		static String READ="Read";
		static String UID = "UID";
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
				 Message.ID+" INTEGER PRIMARY KEY, " +
				 Message.UID + " INTEGER, " +
				 Message.CONTENT+ " VARCHAR(1000), " +
				 Message.SENT_FROM+ " INTEGER, " +
				 Message.SEND_TO+ " INTEGER, " +
				 Message.DATE + " DATETIME, " +
				 Message.READ + " TINYINT(1)" +
				 ");";
		db.execSQL(msg_sql);
	}
	
	public long insertMessage(String content, int userID, int from, int to, String date) {
		long rowID = -1;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(Message.SENT_FROM, from);
		cv.put(Message.SEND_TO, to);
		cv.put(Message.DATE, date);
		cv.put(Message.CONTENT, content);
		cv.put(Message.UID, userID);
		cv.put(Message.READ, 0);
		rowID = db.insert(Message.TABLE, null, cv);
		return rowID;
	}
	
	public ArrayList<MsgRecord> getConversation(int loggedInUser, int otherUser) {
		SQLiteDatabase db = this.getReadableDatabase();
		String uid1 = String.valueOf(loggedInUser);
		String uid2 = String.valueOf(otherUser);
		Cursor cursor = db.query(Message.TABLE, null, "(("+Message.SENT_FROM+"=? AND "+Message.SEND_TO+"= ?) OR ("+Message.SENT_FROM+"=? AND "+Message.SEND_TO+"=?)) AND UID = ?", new String[] {uid1, uid2, uid2, uid1, uid1}, null, null, Message.DATE);
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
	
	public ArrayList<RoomieRecord> getRoomies(int userID) {
		ArrayList<RoomieRecord> roomies = new ArrayList<RoomieRecord>();
		Cursor cursor = getRoomiesWithUnreadCount(userID); //db.query(Roomie.TABLE, null, null, null, null, null, Roomie.COMPATIBILITY+" DESC");
		if(cursor.moveToFirst()) {
			do {
				int uid = cursor.getInt(cursor.getColumnIndex(Roomie.ID));
				String displayName = cursor.getString(cursor.getColumnIndex(Roomie.DISPLAY_NAME));
				String email = cursor.getString(cursor.getColumnIndex(Roomie.EMAIL));
				String phone = cursor.getString(cursor.getColumnIndex(Roomie.PHONE));
				String aboutMe = cursor.getString(cursor.getColumnIndex(Roomie.ABOUT_ME));
				double compatScore = cursor.getDouble(cursor.getColumnIndex(Roomie.COMPATIBILITY));
				String thumbnail = cursor.getString(cursor.getColumnIndex(Roomie.THUMBNAIL_URL));
				int unreadCount = cursor.getInt(cursor.getColumnIndex(Roomie.MSG_COUNT));
				roomies.add(new RoomieRecord(uid, displayName, phone, email, aboutMe, thumbnail, compatScore, unreadCount));
			} while(cursor.moveToNext());
		}
		return roomies;
	}
	
	private Cursor getRoomiesWithUnreadCount(int userID) {
		String sql = "SELECT "+Roomie.TABLE+".*, " +
					 		"(SELECT COUNT(*) FROM "+Message.TABLE+" WHERE "+
					 		Message.UID+" = ?"+ " AND "+Message.SEND_TO+" = ? AND "+Message.READ+" = 0 AND "+Message.SENT_FROM+" = "+Roomie.TABLE+"."+Roomie.ID+") AS '"+Roomie.MSG_COUNT+"' " +
					 "FROM "+Roomie.TABLE +" "+
					 "ORDER BY "+Roomie.MSG_COUNT+" DESC,"+Roomie.COMPATIBILITY+" DESC";
		SQLiteDatabase db = this.getReadableDatabase();
		Log.i("unread_sql", sql);
		return db.rawQuery(sql, new String[] {String.valueOf(userID), String.valueOf(userID) });
	}
	
	private Cursor getRoomieWithUnreadCount(int userID, int roomieID) {
		String sql = "SELECT "+Roomie.TABLE+".*, " +
					 		"(SELECT COUNT(*) FROM "+Message.TABLE+" WHERE "+
					 		Message.UID+" = ?"+ " AND "+Message.SEND_TO+" = ? AND "+Message.SENT_FROM+" = ? AND "+Message.READ+" = 0) AS '"+Roomie.MSG_COUNT+"' " +
					 "FROM "+Roomie.TABLE +" "+
					 "WHERE "+Roomie.ID+" = ? " +
					 "ORDER BY "+Roomie.MSG_COUNT+" DESC,"+Roomie.COMPATIBILITY+" DESC";
		SQLiteDatabase db = this.getReadableDatabase();
		Log.i("unread_sql", sql);
		return db.rawQuery(sql, new String[] {String.valueOf(userID), String.valueOf(userID), String.valueOf(roomieID), String.valueOf(roomieID) });
	}
	
	public void setReadFlag(int user_id, int roomie_id) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(Message.READ, 1);
		db.update(Message.TABLE, cv, Message.UID+"= ? AND "+Message.SENT_FROM+"=?", new String[] {user_id+"", roomie_id+""});
	}
	
	public ArrayList<Integer> getRoomiesWithChatHistory(int userID) {
		ArrayList<Integer> roomie_ids = new ArrayList<Integer>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(Message.TABLE, null, Message.UID+"= ?", new String[] { String.valueOf(userID)}, null, null, null);
		if(cursor.moveToFirst()) {
			do {
				int from = cursor.getInt(cursor.getColumnIndex(Message.SENT_FROM));
				int to = cursor.getInt(cursor.getColumnIndex(Message.SEND_TO));
				if(from != userID && !roomie_ids.contains(from))
					roomie_ids.add(from);
				else if(to != userID && !roomie_ids.contains(to))
					roomie_ids.add(to);
			} while(cursor.moveToNext());
		}
		return roomie_ids;
	}
	
	public RoomieRecord getRoomie(int userID, int roomieID) {
		RoomieRecord roomie = null;
		Cursor cursor = getRoomieWithUnreadCount(userID, roomieID);
		if(cursor.moveToFirst()) {
			int uid = cursor.getInt(cursor.getColumnIndex(Roomie.ID));
			String displayName = cursor.getString(cursor.getColumnIndex(Roomie.DISPLAY_NAME));
			String email = cursor.getString(cursor.getColumnIndex(Roomie.EMAIL));
			String phone = cursor.getString(cursor.getColumnIndex(Roomie.PHONE));
			String aboutMe = cursor.getString(cursor.getColumnIndex(Roomie.ABOUT_ME));
			double compatScore = cursor.getDouble(cursor.getColumnIndex(Roomie.COMPATIBILITY));
			String thumbnail = cursor.getString(cursor.getColumnIndex(Roomie.THUMBNAIL_URL));
			int unreadCount = cursor.getInt(cursor.getColumnIndex(Roomie.MSG_COUNT));
			roomie = new RoomieRecord(uid, displayName, phone, email, aboutMe, thumbnail, compatScore, unreadCount);
		}
		return roomie;
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
