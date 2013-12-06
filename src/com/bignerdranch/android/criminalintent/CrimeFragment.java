package com.bignerdranch.android.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class CrimeFragment extends Fragment {
	private Crime mCrime;
	private EditText mTitleField;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		//This is public so that activities that host this fragment can call it
		super.onCreate(savedInstanceState);					//Save and retrieve its state, just like Activity
		mCrime = new Crime();								//Fragment.onCreate does not inflate the fragment's view
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, 		//This is the method to inflate the fragment's view
			                 Bundle savedInstanceState) {
		
		//layout res ID, fragment's parent, whether to add inflated view to parent's view
		View view  = inflater.inflate(R.layout.fragment_crime, parent, false);
		
		mTitleField = (EditText) view.findViewById(R.id.crime_title);			//Wire up
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
		
		return view;															//Return the inflated view to the hosting activity
	}
}
