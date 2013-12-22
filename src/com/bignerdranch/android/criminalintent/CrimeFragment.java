/*
 * Starts CrimeCameraActivity
 */

package com.bignerdranch.android.criminalintent;

import java.util.Date;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class CrimeFragment extends Fragment {
	private Crime mCrime;
	private EditText mTitleField;
	private Button mDateButton;
	private CheckBox mSolvedCheckBox;
	public static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";
	private static final String DIALOG_DATE = "date";
	private static final String DIALOG_IMAGE = "image";
	private static final int REQUEST_DATE = 0;					//For getting dates
	private static final int REQUEST_PHOTO = 1;					//For getting photos
	private static final int REQUEST_CONTACT = 2;				//For getting the suspect from the contact list
	private static final String TAG = "CrimeFragment";
	
	private ImageButton mPhotoButton;
	private ImageView mPhotoView;
	private Button mSuspectButton;
	private ActionMode.Callback mActionModeCallback;
	
	public static CrimeFragment newInstance(UUID crimeId) {     //Use this to instantiate CrimeFragment instead of its constructor
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_CRIME_ID, crimeId);			
		
		CrimeFragment fragment = new CrimeFragment();		
		fragment.setArguments(args);							//Supply info for this fragment to be instantiated with			
			
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {			//This is public so that activities that host this fragment can call it
		super.onCreate(savedInstanceState);						
		UUID crimeId = (UUID) getArguments()					//Save and retrieve its state, just like Activity
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
				// To start a dialog fragment
				FragmentManager fm = getActivity()								//Get CrimePagerActivity's fragmentManager
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
		
		mPhotoButton = (ImageButton) view.findViewById(R.id.crime_imageButton);
		mPhotoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), CrimeCameraActivity.class);	//Use intent when dealing with Activity
				startActivityForResult(intent, REQUEST_PHOTO);
			}
		});
		
		//If camera is not available, disable camera functionality
		PackageManager pm = getActivity().getPackageManager();
		if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) &&
				!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
			mPhotoButton.setEnabled(false);
		}
		
		mPhotoView = (ImageView) view.findViewById(R.id.crime_imageView);
		mPhotoView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Photo photo = mCrime.getPhoto();
				if (photo == null) return;
				
				FragmentManager fm = getActivity().getSupportFragmentManager();
				String path = getActivity().getFileStreamPath(photo.getFilename()).getAbsolutePath();
				ImageFragment.newInstance(path).show(fm, DIALOG_IMAGE);
			}
		});
		
		// Delete photo from holding down on the mPhotoView icon
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			registerForContextMenu(mPhotoView);
		} else {
			mActionModeCallback = new ActionMode.Callback() {
				
				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					MenuInflater inflater = mode.getMenuInflater();
					inflater.inflate(R.menu.crime_fragment_context_menu, menu);
					return true;
				}
				
				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					return false;
				}
				
				@Override
				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
					switch (item.getItemId()) {
					case R.id.menu_item_delete_photo:
						deleteOldPhoto();
						mode.finish();
						return true;
					default:
						return false;
					}
				}
				
				@Override
				public void onDestroyActionMode(ActionMode mode) {
					//Leave empty		
				}
			};
		}
		
		mPhotoView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				getActivity().startActionMode(mActionModeCallback);
				return true;
			}
		});
		
		//Write a report
		Button reportButton = (Button) view.findViewById(R.id.crime_reportButton);
		reportButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_SEND);					//Implicit intent to get external app's activity
				intent.setType("text/plain");									//Crime report is only a string
				intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
				intent.putExtra(Intent.EXTRA_SUBJECT, 
						getString(R.string.crime_report_subject));
				intent = Intent.createChooser(intent, getString(R.string.send_report));	//Always create a Chooser for the user
				startActivity(intent);											//Get the OS manager to find the most appropriate app
			}
		});
		
		mSuspectButton = (Button) view.findViewById(R.id.crime_suspectButton);
		mSuspectButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_PICK, 					//Pick a contact out of the 
						ContactsContract.Contacts.CONTENT_URI);					//user's contact list
				startActivityForResult(intent, REQUEST_CONTACT);				//Expecting a contact back as a result
			}
		});
		
		if (mCrime.getSuspect() != null) {
			mSuspectButton.setText(mCrime.getSuspect());
		}
		
		Button callButton = (Button) view.findViewById(R.id.crime_callButton);
		callButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_DIAL);
				if (mCrime.getPhoneNum() != null) {
					intent.setData(Uri.parse("tel:" + mCrime.getPhoneNum()));
					startActivity(intent);
				}
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
		else if (requestCode == REQUEST_PHOTO) {
			// Create a new photo object and attach it to the crime
			String filename = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
			if (filename != null) {
				// Delete old photo if it exists
				deleteOldPhoto();
				// Set the new photo on the current Crime
				Photo photo = new Photo(filename);
				mCrime.setPhoto(photo);
				showPhoto();
			}
		}
		else if (requestCode == REQUEST_CONTACT) {
			Uri contactUri = data.getData();
			String hasPhoneNumber = ContactsContract.Contacts.HAS_PHONE_NUMBER;
			String hasNames = ContactsContract.Contacts.DISPLAY_NAME;
			String number = ContactsContract.CommonDataKinds.Phone.NUMBER;
			String ID = ContactsContract.Contacts._ID;
			
			//Specify which fields you want your query to return values for
			String[] queryFields = new String[] {
				ContactsContract.Contacts.DISPLAY_NAME,					//All the display names of the contacts
				ContactsContract.Contacts.HAS_PHONE_NUMBER,
				ContactsContract.Contacts._ID
			};
			
			//Perform your query here - the contactUri is like a "where" clause here
			Cursor cursor = getActivity().getContentResolver()
					.query(contactUri, queryFields, null, null, null);
			
			//Double check that your activity got results
			if (cursor.getCount() == 0) {								//Num of rows in the cursor
				cursor.close(); return;
			}
			
			//Pull out the first column of the first row of data -
			//that is your suspect's name
			cursor.moveToFirst();											//There's only 1 row, because only one contact selected
			String suspect = cursor.getString(cursor.getColumnIndex(hasNames));
			int hasNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(hasPhoneNumber)));
			String suspectID = cursor.getString(cursor.getColumnIndex(ID));	//Contact's ID
			
			if (hasNumber > 0) {											//num of phone numbers recorded
				/*
				 * Phone numbers are stored in their own table and need to be queried separately. 
				 * To query the phone number table use the URI stored in the SDK variable 
				 * ContactsContract.CommonDataKinds.Phone.CONTENT_URI. 
				 */
				String[] selectionArgs = new String[] {suspectID};
				
				cursor = getActivity().getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, 
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", 	//Getting the specific number with ID
						selectionArgs, null);
				
				cursor.moveToFirst();													//Get the first number 
				String phoneNumber = cursor.getString(cursor.getColumnIndex(number));
				mCrime.setPhoneNum(phoneNumber);
			}
			
			mCrime.setSuspect(suspect);
			mSuspectButton.setText(mCrime.getSuspect());
			cursor.close();
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
		case R.id.menu_item_delete_crime:
			deleteOldPhoto();
			CrimeLab.get(getActivity()).deleteCrime(mCrime);
			getActivity().finish();											//Go back to the parent activity upon deletion
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onPause() {													//Save the data whenever the fragment is paused.
		super.onPause();	
		CrimeLab.get(getActivity()).saveCrime();
		
//		//Saved to external storage
//		File file = null;
//		if ((file = getActivity().getExternalFilesDir(null)) != null) {		//Check to see if external storage is available
//			CrimeLab.get(getActivity()).saveToExternal();
//		} else {
//			Log.d(TAG, "External storage not available");
//		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.crime_list_item_context, menu);
	}
	
	private void showPhoto() {
		//(Re)set the image button's image based on our photo
		Photo photo = mCrime.getPhoto();
		BitmapDrawable bitmapdrawable = null;
		
		if (photo != null) {
			String path = getActivity().getFileStreamPath(photo.getFilename()).getAbsolutePath();
			bitmapdrawable = PictureUtils.getScaleDrawable(getActivity(), path);
		}
		mPhotoView.setImageDrawable(bitmapdrawable);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		showPhoto();					//Show the photo as soon as CrimeFragment's view becomes visible
	}
	
	@Override
	public void onStop() {
		super.onStop();
		PictureUtils.cleanImageView(mPhotoView);	//Release the memory given to the imageView object
	}
	
	private void deleteOldPhoto() {
		if (mCrime.getPhoto() != null) {
			getActivity().deleteFile(mCrime.getPhoto().getFilename());
			mCrime.setPhoto(null);
			PictureUtils.cleanImageView(mPhotoView);
			Log.d(TAG, "Deleted photo");
		}
	}
	
	/*
	 * Creates 4 strings, pieces them together to generate a report
	 */
	private String getCrimeReport() {
		String solveString = null;
		if (mCrime.isSolved()) {
			solveString = getString(R.string.crime_report_solved);				//getString is a Fragment's method
		} else {
			solveString = getString(R.string.crime_report_unsolved);
		}
		
		String dateFormat = "EEE, MMM dd";
		String dateString = DateFormat.format(dateFormat, mCrime.getDateObj()).toString();
		
		String suspect = mCrime.getSuspect();
		if (suspect == null) {
			suspect = getString(R.string.crime_report_no_suspect);
		} else {
			suspect = getString(R.string.crime_report_suspect);
		}
		
		//Pieces the 4 strings together
		String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solveString, suspect);
		
		return report;
	}
}
