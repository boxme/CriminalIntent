package com.bignerdranch.android.criminalintent;

import java.util.UUID;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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
	
	public static CrimeFragment newInstance(UUID crimeId) {
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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, 		//This is the method to inflate the fragment's view
			                 Bundle savedInstanceState) {
		
		//layout res ID, fragment's parent, whether to add inflated view to parent's view
		View view  = inflater.inflate(R.layout.fragment_crime, parent, false);
		
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
		
		mDateButton = (Button) view.findViewById(R.id.crime_date);				//Set date button, disabled for now
		mDateButton.setText(mCrime.getDate());
		mDateButton.setEnabled(false);
		
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
}
