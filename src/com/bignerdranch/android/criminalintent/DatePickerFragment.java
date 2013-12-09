package com.bignerdranch.android.criminalintent;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

public class DatePickerFragment extends DialogFragment {
	public static String EXTRA_DATE = "com.bignerdranch.android.criminalintent.date";
	private Date mDate;
	
	public static DatePickerFragment newInstance(Date date) {		//This will be construct an instance of DatePickerFragment
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_DATE, date);						//Pass the date to the fragment
		
		DatePickerFragment fragment = new DatePickerFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mDate = (Date) getArguments().getSerializable(EXTRA_DATE);
		
		/*Create a calendar to get the year, month, and day*/
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mDate);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		
		View view = getActivity().getLayoutInflater()
					.inflate(R.layout.dialog_date, null);
		
		DatePicker datePicker = (DatePicker) view.findViewById(R.id.dialog_date_datePicker);
		datePicker.init(year, month, day, new OnDateChangedListener() {
			@Override
			public void onDateChanged(DatePicker view, int year, int month, int day) {
				//Translate year, month, day into Date object using a callendar
				mDate = new GregorianCalendar(year, month, day).getTime();
				
				//Update arguments to preserve selected value on rotation of phone
				getArguments().putSerializable(EXTRA_DATE, mDate);
			}
		});
		
		return new AlertDialog.Builder(getActivity())				//Fluent interface to create AlertDialog instance
					.setView(view)
					.setTitle(R.string.date_picker_title)			//Two AlertDialog.Builder methods to configure your dialog
//					.setPositiveButton(android.R.string.ok, null)	//There are positive, negative, and neutral button
					.setPositiveButton(android.R.string.ok, 
									   new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {	//When ok button is clicked
							sendResult(Activity.RESULT_OK);							//Authorise to send result
						}
					})
					.create();										//AlertDialog.Builder.create() returns the configured dialog
	}
	
	private void sendResult(int resultCode) {						//Send the data back to its targeted fragment
		if (getTargetFragment() == null) return;
		
		Intent intent = new Intent();
		intent.putExtra(EXTRA_DATE, mDate);
		
		getTargetFragment()											//Calls the targeted fragment's method to handle the data
					.onActivityResult(getTargetRequestCode(), resultCode, intent);
	}
}
