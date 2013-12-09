package com.bignerdranch.android.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DatePickerFragment extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())				//Fluent interface to create AlertDialog instance
					.setTitle(R.string.date_picker_title)			//Two AlertDialog.Builder methods to configure your dialog
					.setPositiveButton(android.R.string.ok, null)	//There are positive, negative, and neutral button
					.create();										//AlertDialog.Builder.create() returns the configured dialog
	}
}
