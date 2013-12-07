package com.bignerdranch.android.criminalintent;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class CrimeListFragment extends ListFragment {
	private ArrayList<Crime> mCrimes;
	private static final String TAG = "CrimeListFragment";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.crime_title);			//getActivity() returns the hosting activity
		mCrimes = CrimeLab.get(getActivity()).getCrimes();		
		
//		ArrayAdapter<Crime> adapter = 
//				new ArrayAdapter<Crime>(getActivity(), 		//Context obj required to use the resource ID that's the second parameter
//										android.R.layout.simple_list_item_1, //Resource ID identifies the layout that's the second paramter
//										mCrimes);			//The data set
		
		CrimeAdapter adapter = new CrimeAdapter(mCrimes);
		setListAdapter(adapter);							//ListFragment method to set the adapter of the implicit ListView
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int pos, long id) {		//Click on the item in ListView
//		Crime crime = (Crime) getListAdapter().getItem(pos);	//ListFragment method to return the adapter 
		Crime crime = ((CrimeAdapter) getListAdapter()).getItem(pos);
		Log.d(TAG, crime.getTitle() + " was clicked");			//that's set on ListFragment's listview
	}
	
	private class CrimeAdapter extends ArrayAdapter<Crime> {
		
		public CrimeAdapter(ArrayList<Crime> c) {
			super(getActivity(), 0, c);					//To properly hook up dataset of Crimes
		}
		
		@Override
		public View getView(int pos, View convertView, ViewGroup parent) {	//Customise how adapter should get info from Crime to make view
			if (convertView == null) {								//If not given a view, inflate one
				convertView = getActivity().getLayoutInflater()		//convertView could be reused if adapter thinks it can reconfigure
						.inflate(R.layout.list_item_crime, null);	//So it might not be null
			}
			
			Crime crime = getItem(pos);
			
			TextView titleTextView = 
					(TextView) convertView.findViewById(R.id.crime_list_item_titleTextView);
			titleTextView.setText(crime.getTitle());
			
			TextView dateTextView = 
					(TextView) convertView.findViewById(R.id.crime_list_item_dateTextView);
			dateTextView.setText(crime.getDate());
			
			CheckBox solvedCheckBox =
					(CheckBox) convertView.findViewById(R.id.crime_list_item_solvedCheckBox);
			solvedCheckBox.setChecked(crime.isSolved());
			
			return convertView;
		}
	}
}
