/*
 * CrimePagerActivity will start the CrimeFragment
 */

package com.bignerdranch.android.criminalintent;

import java.util.ArrayList;
import java.util.UUID;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

/*To handle swiping forward and backward*/

public class CrimePagerActivity extends FragmentActivity {
	private ViewPager mViewPager;
	private ArrayList<Crime> mCrimes;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.viewPager);
		setContentView(mViewPager);
		
		mCrimes = CrimeLab.get(this).getCrimes();						//Get dataset from CrimeLab
		
		FragmentManager fm = getSupportFragmentManager();				//Instantiate this activity's fragmentManager
		mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {		//FragmentStatePagerAdapter is the agent to communicate with ViewPager
			@Override
			public int getCount() {
				return mCrimes.size();
			}
			
			@Override
			public Fragment getItem(int pos) {
				Crime crime = mCrimes.get(pos);
				return CrimeFragment.newInstance(crime.getID());
			}
		});
		
		UUID crimeId = (UUID) getIntent()
				.getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);	//This is saved in CrimeListFragment and retrieve here
		for (int i = 0; i < mCrimes.size(); ++i) {						//Find the crime instance that matches the crimeId in extra
			if (mCrimes.get(i).getID().equals(crimeId)) {
				mViewPager.setCurrentItem(i);
				break;
			}
		}
		
		/*Replace the activity's title that appears on the action bar with the title of the current crime*/
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int pos) {											//Which page is now being selected
				Crime crime = mCrimes.get(pos);
				if (crime.getTitle() != null) {
					setTitle(crime.getTitle());
				}
			}
			
			@Override
			public void onPageScrolled(int pos, float posOffset, int posOffsetPixels) {}	//Where you page is going to be
			
			@Override
			public void onPageScrollStateChanged(int state) {}		//whether the page animation is being actively dragged,
																	//settling to a steady state, or idle.
		});
	}
}
