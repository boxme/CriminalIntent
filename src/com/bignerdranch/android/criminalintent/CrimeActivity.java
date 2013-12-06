package com.bignerdranch.android.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;


public class CrimeActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crime);
		
		FragmentManager fm = getSupportFragmentManager();					//Get the activity's fragmentManager
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);	//If the fragment is already in the list, return it
		
		if (fragment == null) {												//Else, it's a new one, so create a new one and add
			fragment = new Fragment();
			fm.beginTransaction()						//Create a new fragment transaction,
				.add(R.id.fragmentContainer, fragment)	//Include one add operation in it,
				.commit();								//And then commit it
		}
	}

}
