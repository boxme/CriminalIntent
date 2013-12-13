package com.bignerdranch.android.criminalintent;

import java.util.Date;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class CrimeFragment extends Fragment {
	private Crime mCrime;
	private EditText mTitleField;
	private Button mDateButton;
	private CheckBox mSolvedCheckBox;
	public static final String EXTRA_CRIME_ID = 
			"com.bignerdranch.android.criminalintent.crime_id";
	private static final String DIALOG_DATE = "date";
	private static final int REQUEST_DATE = 0;
	
	public static CrimeFragment newInstance(UUID crimeId) {     //Use this to instantiate CrimeFragment instead of its constructor
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_CRIME_ID, crimeId);
		
		CrimeFragment fragment = new CrimeFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {			//This is public so that activities that host this fragment can call it
		super.onCreate(savedInstanceState);						//Save and retrieve its state, just like Activity
		UUID crimeId = (UUID) getArguments()
						.getSerializable(EXTRA_CRIME_ID);
//		mCrime = new Crime();									//Fragment.onCreate does not inflate the fragment's view
		mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);	//Get the crime from CrimeLab using the crimeId.
		
		setHasOptionsMenu(true);								//CrimeFragment will also implement option menu
	}

	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, 		//This is the method to inflate the fragment's view
			                 Bundle savedInstanceState) {
		
		//layout res ID, fragment's parent, whether to add inflated view to parent's view
		View view  = inflater.inflate(R.layout.fragment_crime, parent, false);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (NavUtils.getParentActivityName(getActivity()) != null) {		//If there exists a parent Activity
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);   //Set App icon as a button
			}
		}
		
		mTitleField = (EditText) view.findViewById(R.id.crime_title);			//Wire up
		mTitleField.setText(mCrime.getTitle());
		mTitleField.addTextChangedListener(new TextWatcher() {					//Text changed listener
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mCrime.setTitle(s.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				//Blank
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				//Blank
			}
		});
		
		mDateButton = (Button) view.findViewById(R.id.crime_date);				
		updateDate();
//		mDateButton.setEnabled(false);
		mDateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity()
									.getSupportFragmentManager();
				DatePickerFragment dialog = DatePickerFragment
											.newInstance(mCrime.getDateObj());
				dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);		//Set CrimeFragment the target fragment
				dialog.show(fm, DIALOG_DATE);									//Set tag on DialogFragment in FragmentManager
				
//				PickerFragment dialog = PickerFragment.newInstance(mCrime.getDateObj());
//				dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
//				dialog.show(fm, DIALOG_DATE);
			}
		});
		
		mSolvedCheckBox = (CheckBox) view.findViewById(R.id.crime_solved);		//Checkbox to update the solved status of a crime
		mSolvedCheckBox.setChecked(mCrime.isSolved());
		mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// Set the crime's solved property
				mCrime.setSolved(isChecked);
			}
		});
		
		return view;															//Return the inflated view to the hosting activity
	}
	
	/*Handles the data sent by from another Fragment which has targeted this fragment as its target*/
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) return;
		if (requestCode == REQUEST_DATE) {
			Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			mCrime.setDate(date);
			updateDate();
		}
	}
	
	private void updateDate() {
		mDateButton.setText(mCrime.getDate());
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (NavUtils.getParentActivityName(getActivity()) != null) {	//If there exists a parent activity
				NavUtils.navigateUpFromSameTask(getActivity());				//Go back up to that parent activity
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onPause() {													//Save the data whenever the fragment is paused.
		super.onPause();	
		CrimeLab.get(getActivity()).saveCrime();							
	}
}
