package com.bignerdranch.android.criminalintent;

import java.util.Date;
import java.util.UUID;

public class Crime {
	private UUID mID;
	private String mTitle;
	private Date mDate;
	private boolean mSolved;
	
	public Crime() {
		//Generate unique identifier
		mID = UUID.randomUUID();
		mDate = new Date();
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
}
