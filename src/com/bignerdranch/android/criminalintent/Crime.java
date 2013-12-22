package com.bignerdranch.android.criminalintent;

import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class Crime {
	private static final String JSON_ID = "id";
	private static final String JSON_TITLE = "title";
	private static final String JSON_SOLVED = "solved";
	private static final String JSON_DATE = "date";
	private static final String JSON_PHOTO = "photo";
	private static final String JSON_SUSPECT = "suspect";
	private static final String JSON_SUSPECT_NUM = "contact";
	
	private UUID mID;
	private String mTitle;
	private Date mDate;
	private boolean mSolved;
	private Photo mPhoto;
	private String mSuspect;
	private String mContactNumber;
	
	public Crime() {
		//Generate unique identifier
		mID = UUID.randomUUID();
		mDate = new Date();
		mPhoto = null;
		mContactNumber = null;
	}
	
	public Crime(JSONObject json) throws JSONException {	//Loading back from json object
		mID = UUID.fromString(json.getString(JSON_ID));
		mTitle = json.getString(JSON_TITLE);
		mSolved = json.getBoolean(JSON_SOLVED);
		mDate = new Date(json.getLong(JSON_DATE));
		if (json.has(JSON_PHOTO)) {
			mPhoto = new Photo(json.getJSONObject(JSON_PHOTO));
		}
		if (json.has(JSON_SUSPECT)) {
			mSuspect = json.getString(JSON_SUSPECT);
		}
		if (json.has(JSON_SUSPECT_NUM)) {
			mContactNumber = json.getString(JSON_SUSPECT_NUM);
		}
	}
	
	public JSONObject toJSON() throws JSONException {		//Convert crime object into json object
		JSONObject json = new JSONObject();
		json.put(JSON_ID, mID.toString());
		json.put(JSON_TITLE, mTitle);
		json.put(JSON_SOLVED, mSolved);
		json.put(JSON_DATE, mDate.getTime());
		json.put(JSON_SUSPECT, mSuspect);
		json.put(JSON_SUSPECT_NUM, mContactNumber);
		if (mPhoto != null) {
			json.put(JSON_PHOTO, mPhoto.toJSON());
		}		
		return json;
	}
	
	public UUID getID() {
		return mID;
	}

	public String getTitle() {
		return mTitle;
	}
	
	public void setTitle(String title) {
		mTitle = title;
	}
	
	public String getDate() {
		String[] date_array = mDate.toString().split(" ");
		String date_string = date_array[0] + ", " + date_array[1] + " " + 
							 date_array[2] + ", " + date_array[date_array.length-1] + " " +
							 date_array[3] + ".";
		
		return date_string;
	}
	public Date getDateObj() {
		return mDate;
	}
	
	public void setDate(Date date) {
		mDate = date;
	}
	
	public boolean isSolved() {
		return mSolved;
	}
	
	public void setSolved(boolean solved) {
		mSolved = solved;
	}
	
	@Override
	public String toString() {
		return mTitle;
	}
	
	public Photo getPhoto() {
		return mPhoto;
	}
	
	public void setPhoto(Photo photo) {
		mPhoto = photo;
	}
	
	public String getSuspect() {
		return mSuspect;
	}
	
	public void setSuspect(String suspect) {
		mSuspect = suspect;
	}
	
	public String getPhoneNum() {
		return mContactNumber;
	}
	
	public void setPhoneNum(String phoneNum) {
		mContactNumber = phoneNum;
	}
}
