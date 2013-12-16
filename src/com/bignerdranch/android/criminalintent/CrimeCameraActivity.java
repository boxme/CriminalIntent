/*
 * CrimeCameraActivity will be started by CrimeFragment and starts CrimeCameraFragment
 */

package com.bignerdranch.android.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

public class CrimeCameraActivity extends SingleFragmentActivity {
	
	/*
	 * Calls to requestWindowFeature(..) and addFlags(..) must be called before
	 * activity's view is created in Activity.setContentView(..), which in this case
	 * is called in the superclass's implementation of onCreate(..) in CrimeCameraFragment
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//Hide the window title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//Hide the status bar and other os-level chrome
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected Fragment createFragment() {
		return new CrimeCameraFragment();
	}

}
