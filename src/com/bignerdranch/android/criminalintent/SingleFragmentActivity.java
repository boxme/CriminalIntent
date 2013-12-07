package com.bignerdranch.android.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public abstract class SingleFragmentActivity extends FragmentActivity {
	protected abstract Fragment createFragment();							//Abstract method for subclasses to instantiate the fragment
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);
		
		FragmentManager fm = getSupportFragmentManager();					//Get the activity's fragmentManager
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);	//If the fragment is already in the list, return it
		
		if (fragment == null) {
			fragment = createFragment();
			fm.beginTransaction()						//Create a new fragment transaction,
			.add(R.id.fragmentContainer, fragment)		//Include one add operation in it,
			.commit();									//And then commit it
		}
	}
}
