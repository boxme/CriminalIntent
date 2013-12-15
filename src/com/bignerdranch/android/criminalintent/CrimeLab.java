package com.bignerdranch.android.criminalintent;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;
import android.util.Log;

/*
 * A singleton is a class with a private constructor
 */
public class CrimeLab {	
	private static final String TAG = "CrimeLab";
	private static final String FILENAME = "crimes.json";
	
	
	private ArrayList<Crime> mCrimes;
	private CriminalIntentJSONSerializer mSerializer;
	private static CrimeLab sCrimeLab;
	private Context mAppContext;
	
	private CrimeLab(Context appContext) {
		mAppContext = appContext;
		mSerializer = new CriminalIntentJSONSerializer(mAppContext, FILENAME);
//		mCrimes = new ArrayList<Crime>();
		
		try {
			mCrimes = mSerializer.loadCrimes();
			Log.d(TAG, "Loading crimes from file");
		} catch (Exception e) {
			mCrimes = new ArrayList<Crime>();
			Log.d(TAG, "Error loading crimes: ", e);
		}
	}
	
	public static CrimeLab get(Context c) {
		if (sCrimeLab == null) {									//If no single instance of CrimeLab exists, create it first
			sCrimeLab = new CrimeLab(c.getApplicationContext());
		} 
		return sCrimeLab;
	}
	
	public ArrayList<Crime> getCrimes() {
		return mCrimes;
	}
	
	public Crime getCrime(UUID id) {
		for (Crime crime : mCrimes) {
			if (crime.getID().equals(id)) 
				return crime;
		}
		return null;
	}
	
	public void addCrime(Crime crime) {
		mCrimes.add(crime);
	}
	
	public boolean saveCrime() {
		try {
			mSerializer.saveCrimes(mCrimes);
			Log.d(TAG, "crime saved to file");
			return true;
		} catch (Exception e) {
			Log.d(TAG, "Error saving crimes: ", e);				//Logging the error for failing to save
			return false;										//In real life, you should alert the user if the saving fails.
		}
	}
	
	public boolean saveToExternal() {
		try {
			mSerializer.savedToExternal(mCrimes);
			Log.d(TAG, "crime saved to external file");
			return true;
		} catch (Exception e) {
			Log.d(TAG, "Error saving crimes to external: ", e);
			return false;
		}
	}
	
	public void deleteCrime(Crime crime) {
		mCrimes.remove(crime);
	}
}
