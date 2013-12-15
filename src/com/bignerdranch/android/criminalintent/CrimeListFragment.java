package com.bignerdranch.android.criminalintent;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class CrimeListFragment extends ListFragment {
	private ArrayList<Crime> mCrimes;
	private boolean mSubtitleVisible;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.crime_title);			//getActivity() returns the hosting activity
		setHasOptionsMenu(true);								//Tell fragment needs to receive option menu callbacks
		
		mCrimes = CrimeLab.get(getActivity()).getCrimes();
		
//		ArrayAdapter<Crime> adapter = 
//		new ArrayAdapter<Crime>(getActivity(), 		//Context obj required to use the resource ID that's the second parameter
//								android.R.layout.simple_list_item_1, //Resource ID identifies the layout that's the second paramter
//								mCrimes);			//The data set

		CrimeAdapter adapter = new CrimeAdapter(mCrimes);
		setListAdapter(adapter);							//ListFragment method to set the adapter of the implicit ListView
		setRetainInstance(true);							//Use this method to retain the state
		mSubtitleVisible = false;
	}
	
	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
//		View view = super.onCreateView(inflater, parent, savedInstanceState);
				
		View view = inflater.inflate(R.layout.fragment_crime_list, parent, false);	//@android:id/empty & @android:id/list
																					//included in the XML file will determine
		Button addButton = (Button) view.findViewById(R.id.add_new_crime_button);	//the view to display automatically
		addButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addCrime();
			}
		}) ;
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (mSubtitleVisible) {												//If subtitle is shown before rotation
				getActivity().getActionBar().setSubtitle(R.string.subtitle);
			}
		}
		//Cannot use ListFragment.getListView() because it returns null until after onCreateView(..) is returned
		ListView listView = (ListView) view.findViewById(android.R.id.list);	//Retrieve the listview managed by ListFragment
		
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			//Use floating context menu on Froyo and Gingerbread
			registerForContextMenu(listView);										//Register listview for context menu
		} else {
			//Use contextual action bar on Honeycomb and above
			listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);			//Enable multiple list items selection 
			
			listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

				@Override
				public void onItemCheckedStateChanged(ActionMode mode, int position,
						long id, boolean checked) {
					// Required but not used in this implementation
				}			
				
				// ActionMode.Callback methods
				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {		//ActionMode has details for configuring contextual action bar
					MenuInflater inflater = mode.getMenuInflater();					//Get MenuInflater from mode than activity
					inflater.inflate(R.menu.crime_list_item_context, menu);
					return true;
				}
				
				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					// Required but not used in this implementation
					return false;
				}
				
				//Delete one or more crimes from crimelab and then reload the list
				@Override
				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
					switch (item.getItemId()) {
					case R.id.menu_item_delete_crime:
						CrimeAdapter adapter = (CrimeAdapter) getListAdapter();
						CrimeLab crimeLab = CrimeLab.get(getActivity());
						for (int i = adapter.getCount() - 1; i >= 0; --i) {
							if (getListView().isItemChecked(i)) {
								crimeLab.deleteCrime(adapter.getItem(i));
							}
						}
						mode.finish();												//Prepare action mode to be destroyed
						adapter.notifyDataSetChanged();
						return true;
					default:
						return false;
					}
				}
				
				@Override
				public void onDestroyActionMode(ActionMode mode) {
					// Required but not used in this implementation			
				}

			});
		}
		return view;
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
	
	/* Inner class. Also a Singleton
	 * Adapter to connect the fragment with the data
	 */
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
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {	//For startActivityForResult(Intent)
		((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
	}
	
	/*
	 * Create Options Menu at action bar
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {				//Option menu callback on fragment
		super.onCreateOptionsMenu(menu, inflater);									//Recommended way
		inflater.inflate(R.menu.fragment_crime_list, menu);							//Populates the Menu instance
		
		MenuItem showSubtitle = menu.findItem(R.id.menu_item_show_subtitles);
		if (mSubtitleVisible && showSubtitle != null) {								//If the subtitle is shown before rotation
			showSubtitle.setTitle(R.string.hide_subtitles);
		}
	}
	
	@TargetApi(11)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {							//When the item in the option menu is pressed
		switch (item.getItemId()) {
		case R.id.menu_item_new_crime:
			addCrime();
			return true;
		case R.id.menu_item_show_subtitles:
			if (getActivity().getActionBar().getSubtitle() == null) {
				getActivity().getActionBar().setSubtitle(R.string.subtitle);		//Set subtitle
				item.setTitle(R.string.hide_subtitles);								//show the hide subtitle string
				mSubtitleVisible = true;
			}
			else {
				getActivity().getActionBar().setSubtitle(null);						//Show no subtitle
				item.setTitle(R.string.show_subtitles);								//show the show subtitle string
				mSubtitleVisible = false;
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	/*
	 * Create Context Menu for delete.
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		/*
		 * If there are multiple context menu resource, determine which to inflate by checking the ID
		 * of the view that is passed in
		 */
		getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context, menu);
	}
	
	/*
	 * Actions to take when item on context menu is selected
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();	//Returns AdapterContextMenuInfo because
		int pos = info.position;													//ListView is a subclass of AdapterView
		
		CrimeAdapter adapter = (CrimeAdapter) getListAdapter();
		Crime crime = adapter.getItem(pos);											//Retrieve the right crime
		
		switch (item.getItemId()) {
		case R.id.menu_item_delete_crime:											//Only one ID of context menu here to consider
			CrimeLab.get(getActivity()).deleteCrime(crime);
			adapter.notifyDataSetChanged();
			return true;
		}
		
		return super.onContextItemSelected(item);
	}
	
	private void addCrime() {
		Crime crime = new Crime();
		CrimeLab.get(getActivity()).addCrime(crime);
		Intent intent = new Intent(getActivity(), CrimePagerActivity.class);	
		intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getID());			//Tell CrimePager to tell which crime to show
		startActivityForResult(intent, 0);										//in CrimeFragment

	}
}
