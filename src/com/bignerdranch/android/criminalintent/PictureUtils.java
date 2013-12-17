package com.bignerdranch.android.criminalintent;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.widget.ImageView;


public class PictureUtils {
	/*
	 * Get a BitmapDrawable from a local file that's scaled down
	 * to fit the current Window size
	 */
	@SuppressWarnings("deprecation")
	public static BitmapDrawable getScaleDrawable(Activity activity, String path) {
		Display display = activity.getWindowManager().getDefaultDisplay();
		float destWidth = display.getWidth();
		float destHeight = display.getHeight();
		
		//Read in the dimensions of the image on disk
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		
		float srcWidth = options.outWidth;
		float srcHeight = options.outHeight;
		
		int inSampleSize = 1;
		if (srcHeight > destHeight || srcWidth > destWidth) {
			if (srcWidth > srcHeight) {
				inSampleSize = Math.round(srcHeight / destHeight);
			} else {
				inSampleSize = Math.round(srcWidth / destWidth);
			}
		}
		
		options = new BitmapFactory.Options();
		options.inSampleSize = inSampleSize;
		
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		return new BitmapDrawable(activity.getResources(), bitmap);
	}
	
	//Clean up an ImageView's BitmapDrawable, if it has one
	public static void cleanImageView(ImageView imageView) {
		if (!(imageView.getDrawable() instanceof BitmapDrawable)) {
			return;
		}
		
		//Clean up the view's image for the sake of memory
		BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
		bitmapDrawable.getBitmap().recycle();	//this call is necessary
		imageView.setImageDrawable(null);									
	}
}
