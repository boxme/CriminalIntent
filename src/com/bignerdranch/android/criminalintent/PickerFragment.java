package com.bignerdranch.android.criminalintent;

import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class PickerFragment extends DialogFragment {
	private Date mDate;
	public static String EXTRA_DATE = "com.bignerdranch.android.criminalintent.date";
	
	public static PickerFragment newInstance(Date date) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_DATE, date);
		
		PickerFragment fragment = new PickerFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mDate = (Date) getArguments().getSerializable(EXTRA_DATE);
		
		return new AlertDialog.Builder(getActivity())				
		.setTitle("Change Option")
		.setMessage("Change the date or time of the crime")
		.setPositiveButton(R.string.time_picker_option, null)
		.setNegativeButton(R.string.date_picker_option, null)
		.create();
	}
}
