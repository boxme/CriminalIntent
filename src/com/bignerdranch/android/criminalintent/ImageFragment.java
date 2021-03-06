package com.bignerdranch.android.criminalintent;

import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageFragment extends DialogFragment {
	public static final String EXTRA_IMAGE_PATH = 
			"com.bignerdranch.android.criminalintent.image_path";
	private static final String TAG = "ImageFragment";
	
	private ImageView mImageView;
	
	public static ImageFragment newInstance(String imagePath) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_IMAGE_PATH, imagePath);
		
		ImageFragment fragment = new ImageFragment();
		fragment.setArguments(args);
		fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);		//To achieve the minimalist look
		
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflate, ViewGroup parent, Bundle savedInstanceState) {
		mImageView = new ImageView(getActivity());
		
		String path = (String) getArguments().getSerializable(EXTRA_IMAGE_PATH);
		BitmapDrawable image = PictureUtils.getScaleDrawable(getActivity(), path);
		
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			Log.i(TAG, exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION));
		} catch (Exception e) {
			Log.d(TAG, "Error using exifInterface");
		}
		mImageView.setImageDrawable(image);
		
		return mImageView;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		PictureUtils.cleanImageView(mImageView);
	}
}
