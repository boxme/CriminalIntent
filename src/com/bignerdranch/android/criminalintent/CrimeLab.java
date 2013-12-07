package com.bignerdranch.android.criminalintent;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;

/*
 * A singleton is a class with a private constructor
 */
public class CrimeLab {	
	private ArrayList<Crime> mCrimes;
	private static CrimeLab sCrimeLab;
	private Context mAppContext;
	
	private CrimeLab(Context appContext) {
		mAppContext = appContext;
		mCrimes = new ArrayList<Crime>();
		
		for (int i = 0; i < 100; i++) {								//Set 100 crimes
			Crime c = new Crime();
			c.setTitle("Crime #" + i);
			c.setSolved(i % 2 == 0);
			mCrimes.add(c);
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
}
