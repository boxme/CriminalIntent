package com.bignerdranch.android.criminalintent;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class CrimeListFragment extends ListFragment {
	private ArrayList<Crime> mCrimes;
	
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
//		Crime crime = (Crime) getListAdapter().getItem(pos);				//ListFragment method to return the adapter 
		Crime crime = ((CrimeAdapter) getListAdapter()).getItem(pos);		//Get the Crime from the adapter
		
		/*Start CrimePagerActivity*/
		Intent intent = new Intent(getActivity(), CrimePagerActivity.class);
		/*Start CrimeActivity*/
//		Intent intent = new Intent(getActivity(), CrimeActivity.class);		//getActivity in a Fragment returns the Activity
																			//it is associated with
		intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getID());		//Tell CrimeFragment which Crime to display
//		startActivity(intent);	
		startActivityForResult(intent, 0);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
	}
	
	private class CrimeAdapter extends ArrayAdapter<Crime> {
		public CrimeAdapter(ArrayList<Crime> c) {
			super(getActivity(), android.R.layout.simple_list_item_1, c);	//To properly hook up dataset of Crimes
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
	
//	@Override
//	public void onResume() {										//for startActivity(Intent)
//		super.onResume();
//		((CrimeAdapter) getListAdapter()).notifyDataSetChanged();			
//	}
}
