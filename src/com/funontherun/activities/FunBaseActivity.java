package com.funontherun.activities;

import org.holoeverywhere.app.Activity;

import android.os.Bundle;

/**
 * A Base Activity class containing common methods and elements.
 */
public class FunBaseActivity extends Activity {


	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockFragmentActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}