package com.bignerdranch.android.criminalintent;

import java.util.UUID;

import android.support.v4.app.Fragment;

/*Replaced by CrimePagerActivity*/

public class CrimeActivity extends SingleFragmentActivity {
	
	@Override
	protected Fragment createFragment() {
//		return new CrimeFragment();
		UUID crimeId = (UUID) getIntent()
						.getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
		return CrimeFragment.newInstance(crimeId);								//Not calling CrimeFragment constructor directly
	}
}
