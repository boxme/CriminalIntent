package com.bignerdranch.android.criminalintent;

import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

public class PickerFragment extends DialogFragment {
	private Date mDate;
	public static final String EXTRA_DATE = "com.bignerdranch.android.criminalintent.date";
	private static final String DIALOG_DATE = "date";
	public static final int REQUEST_DATE = 0;
	
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
		.setPositiveButton(R.string.time_picker_option, 
						   new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				TimePickerFragment timeDialog = TimePickerFragment.newInstance(mDate);
				startDialog(timeDialog);
			}
		})
		.setNegativeButton(R.string.date_picker_option, 
						   new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				DatePickerFragment dateDialog = DatePickerFragment.newInstance(mDate);
				startDialog(dateDialog);
			}
		})
		.create();
	}
	
	private void startDialog(DialogFragment dialog) {
		FragmentManager fm = getActivity()
				.getSupportFragmentManager();
		dialog.setTargetFragment(this, REQUEST_DATE);
		dialog.show(fm, DIALOG_DATE);
	}
	
	private void sendResult(int resultCode) {
		if (getTargetFragment() == null) return;
		
		Intent intent = new Intent();
		intent.putExtra(EXTRA_DATE, mDate);
		
		getTargetFragment()
			.onActivityResult(getTargetRequestCode(), resultCode, intent);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) return;
		
		if (requestCode == REQUEST_DATE) {
			mDate = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			sendResult(Activity.RESULT_OK);
		}
	}
}
