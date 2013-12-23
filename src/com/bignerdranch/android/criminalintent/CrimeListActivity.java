/*
 * CrimeListActivity will start CrimeListFragment
 */
package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callback,
																		 CrimeFragment.Callback {

	@Override
	protected Fragment createFragment() {
		return new CrimeListFragment();
	}
	
	@Override
	protected int getLayoutResID() {
		return R.layout.activity_masterdetail;
	}
	
	@Override
	public void onCrimeSelected(Crime crime) {
		if (findViewById(R.id.detailFragmentContainer) == null) {				//If layout does not have detailFragmentContainer
			// Start an instance of CrimePagerActivity							//Starts CrimePagerActivity
			Intent intent = new Intent(this, CrimePagerActivity.class);
			intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getID());
			startActivityForResult(intent, 0);
		} else {
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			
			Fragment oldDetail = fm.findFragmentById(R.id.detailFragmentContainer);
			Fragment newDetail = CrimeFragment.newInstance(crime.getID());
			
			if (oldDetail != null) {											//Removes existing CrimeFragment and adds
				ft.remove(oldDetail);											//the new CrimeFragment selected
			}
			
			ft.add(R.id.detailFragmentContainer, newDetail);
			ft.commit();
		}
	}
	
	@Override
	public void onCrimeUpdated(Crime crime) {									//Refresh crime list
		FragmentManager fm = getSupportFragmentManager();
		CrimeListFragment listFragment = (CrimeListFragment)
				fm.findFragmentById(R.id.fragmentContainer);
		listFragment.updateUI();
	}

}
