/*
 * CrimeCameraFragment will start Camera
 */
package com.bignerdranch.android.criminalintent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class CrimeCameraFragment extends Fragment {
	private static final String TAG = "CrimeCameraFragment";
	public static final String EXTRA_PHOTO_FILENAME = 
			"com.bignerdranch.android.criminalintent.photo_filename";
	
	private Camera mCamera;
	private SurfaceView mSurfaceView;
	private View mProgressContainer;
	private OrientationEventListener mOrientationEventListener;
	
	// The two of three parameters of public final void CAMERA.takePicture();
	private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
		@Override
		public void onShutter() {
			// Display the progress indicator
			mProgressContainer.setVisibility(View.VISIBLE);
		}
	};
	
	private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// Create a filename
			String filename = UUID.randomUUID().toString() + ".jpg";
			// Save the jpeg data to disk
			FileOutputStream os = null;
			boolean success = true;
			try {
				os = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
				os.write(data);
			} catch (Exception e) {
				Log.e(TAG, "Error writing to file " + filename, e);
				success = false;
			} finally {
				try {
					if (os != null) {
						os.close();
					}
				} catch (Exception e) {
					Log.e(TAG, "Closing file error " + filename, e);
					success = false;
				}
			}
			if (success) {
				// Set photo filename on the result intent to pass back to CrimeFragment
				Intent intent = new Intent();
				intent.putExtra(EXTRA_PHOTO_FILENAME, filename);
				getActivity().setResult(Activity.RESULT_OK, intent);			//Pass back to CrimeFragment
				Log.i(TAG, "JPEG saved at " + filename);
			} else {
				getActivity().setResult(Activity.RESULT_CANCELED);
			}
			getActivity().finish();
		}
	};
	
	@Override
	@SuppressWarnings("deprecation")
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_crime_camera, parent, false);
		
		mProgressContainer = (View) view.findViewById(R.id.crime_camera_progressContainer);		//Progress Bar
		mProgressContainer.setVisibility(View.INVISIBLE);										//Set Invisible first
		
		Button takePictureButton = (Button) view.findViewById(R.id.crime_camera_take_pictureButton);
		takePictureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCamera != null) {
					mCamera.takePicture(mShutterCallback, null, mJpegCallback);
				}
			}
		});
		
		mSurfaceView = (SurfaceView) view.findViewById(R.id.crime_camera_surfaceView);
		SurfaceHolder holder = mSurfaceView.getHolder();						//Connection to Surface object.
		//setType() and SURFACE_TYPE_PUSH_BUFFERS are both deprecated, 			//Surface represents a buffer of raw pixel data
		//but are required for Camera preview to work on pre-Honeycomb devices
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.addCallback(new SurfaceHolder.Callback() {						//SurfaceHolder.Callback() listens for events
			@Override															//in the lifecycle of the Surface
			public void surfaceCreated(SurfaceHolder holder) {					//This is where you connect Surface with its client
				//Tell the camera to use this surface as its preview area
				try {
					if (mCamera != null) {
						mCamera.setPreviewDisplay(holder);						//Set the surface to be used for preview
					}
				} catch (IOException e) {
					Log.e(TAG, "Error setting up preview display", e);
				}
			}
			
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width,	//Tell Surface's client how big is the drawing
					int height) {													//area
				if (mCamera == null) return;
				
				//Set the orientation of the camera
				mOrientationEventListener = new OrientationEventListener(getActivity(), SensorManager.SENSOR_DELAY_NORMAL) {
					@TargetApi(Build.VERSION_CODES.GINGERBREAD)
					@Override
					public void onOrientationChanged(int orientation) {
						if (mCamera == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD)
							return;
						
						CameraInfo info = new CameraInfo();
						Camera.getCameraInfo(CameraInfo.CAMERA_FACING_BACK, info);
						orientation = (orientation + 45) / 90 * 90;
						int rotation = 0;
						rotation = (info.orientation + orientation) % 360;
						
						Parameters parameters = mCamera.getParameters();
						parameters.setRotation(rotation);
						mCamera.setParameters(parameters);
						mCamera.setDisplayOrientation(rotation);
					}
				};
				mOrientationEventListener.enable();
				
				//The surface has changed size; update the camera preview size
				Camera.Parameters parameters = mCamera.getParameters();			//Returns current settings of camera
				// Find the best support preview size to be taken
				Size size = getBestSupportedSize(parameters.getSupportedPreviewSizes(), width, height);
				parameters.setPreviewSize(size.width, size.height);
				
				// Find the best supported picture size to be taken
				size = getBestSupportedSize(parameters.getSupportedPictureSizes(), width, height);
				parameters.setPictureSize(size.width, size.height);
				mCamera.setParameters(parameters);
				
				try {
					mCamera.startPreview();										//Starts drawing frames on the Surface
				} catch (Exception e) {
					Log.e(TAG, "Could not start preview", e);
					mCamera.release();											//Disconnects and returns camera resource
					mCamera = null;
				}
			}
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {				//Tell Surface's client to stop using Surface
				//No longer display on this surface, stop the preview
				if (mCamera != null) {
					mCamera.stopPreview();										//Stop the preview 
				}
			}
		});
		
		return view;
	}
	
	/*
	 * Open the camera in the main thread
	 */
	@TargetApi(9)
	@Override
	public void onResume() {
		super.onResume();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			mCamera = Camera.open(0);									//For API 9 and above. Open first camera available on device
		} else {
			mCamera = Camera.open();									//For API 8
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		if (mCamera != null) {
			mCamera.release();											//Close the camera
			mCamera = null;
		}
		if (mOrientationEventListener != null) {
			mOrientationEventListener.disable();
			mOrientationEventListener = null;
		}
	}
	
	/*
	 * A simple algo to get the largest size availabe. For a more robust version,
	 * see the CameraPreview.java in the ApiDemos sample app from Android.
	 */
	private Size getBestSupportedSize(List<Size> sizes, int width, int height) {
		Size bestSize = sizes.get(0);
		int largestArea = bestSize.width * bestSize.height;
		for (Size size : sizes) {
			int area = size.height * size.width;
			if (area > largestArea) {
				bestSize = size;
				largestArea = area;
			}
		}
		return bestSize;
	}
}
