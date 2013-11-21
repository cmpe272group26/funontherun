package com.funontherun.activities;

import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.Toast;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;

import com.actionbarsherlock.app.ActionBar;
import com.funontherun.ApplicationEx;
import com.funontherun.R;
import com.funontherun.entities.Location;
import com.funontherun.services.RetrieveDestRouteLocationService;
import com.funontherun.services.RetrieveDestRouteLocationService.RetrieveDestRouteLocationServiceListener;
import com.funontherun.services.RetrieveSrcRouteLocationService;
import com.funontherun.services.RetrieveSrcRouteLocationService.RetrieveSrcRouteLocationServiceListener;

public class HomeActivity extends FunBaseActivity implements
		RetrieveSrcRouteLocationServiceListener,
		RetrieveDestRouteLocationServiceListener {
	/**
	 * Provides Action bar UI feel from Android version 1.6
	 */
	private ActionBar actionBarSherlock;
	/**
	 * Edit Text to enter Source Location Information
	 */
	public EditText sourceEditText;
	/**
	 * Edit Text to enter Destination Location Information
	 */
	public EditText destEditText;
	private Button submitButton;
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		actionBarSherlock = getSupportActionBar();

		actionBarSherlock.setTitle(getResources().getString(R.string.app_name));
		actionBarSherlock.setHomeButtonEnabled(false);
		/**
		 * whether to show Standard Home Icon or not
		 */
		actionBarSherlock.setDisplayHomeAsUpEnabled(false);
		// actionBarSherlock.setBackgroundDrawable(getResources().getDrawable(
		// R.drawable.nav_bar));

		sourceEditText = (EditText) findViewById(R.id.from_route);
		destEditText = (EditText) findViewById(R.id.to_route);
		submitButton = (Button) findViewById(R.id.submit);
		submitButton.setOnClickListener(onClickListner);

		/**
		 * On Key Listener that takes source & destination location on the click
		 * of Done button for further processing
		 */
		destEditText.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				/**
				 * If the event is a key-down event on the "enter" button
				 */
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					ApplicationEx.sourceLoc = sourceEditText.getText()
							.toString();
					ApplicationEx.destinationLoc = destEditText.getText()
							.toString();
					if (validateUserLocation(ApplicationEx.sourceLoc,
							ApplicationEx.destinationLoc))
						getSourceLocation();
					return true;
				}
				return false;
			}
		});

	}

	/**
	 * Web service call for obtaining lattitude & longitude of source location
	 */
	public void getSourceLocation() {
		pd = ProgressDialog.show(HomeActivity.this, "", "Loading...", true);

		/**
		 * Service for obtaining lattitude & longitude of source location
		 */
		RetrieveSrcRouteLocationService srcService = new RetrieveSrcRouteLocationService(
				getApplicationContext(), ApplicationEx.sourceLoc);
		srcService.setListener(this);
		ApplicationEx.operationsQueue.execute(srcService);
	}

	/**
	 * 
	 * @param src
	 * @param dest
	 * @return true if the user has entered valid source & destination locations
	 */
	public boolean validateUserLocation(String src, String dest) {
		src = src.trim();
		dest = dest.trim();
		if (TextUtils.isEmpty(src.trim()) && TextUtils.isEmpty(dest.trim())) {
			Toast.makeText(HomeActivity.this,
					getResources().getString(R.string.location_error_message),
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (TextUtils.isEmpty(src.trim())) {
			Toast.makeText(
					HomeActivity.this,
					getResources().getString(
							R.string.src_location_error_message),
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (TextUtils.isEmpty(dest.trim())) {
			Toast.makeText(
					HomeActivity.this,
					getResources().getString(
							R.string.dest_location_error_message),
					Toast.LENGTH_SHORT).show();
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Web service call for obtaining lattitude & longitude of destination
	 * location
	 */
	public void getDestination() {
		RetrieveDestRouteLocationService destService = new RetrieveDestRouteLocationService(
				getApplicationContext(), ApplicationEx.destinationLoc);
		destService.setListener(this);
		ApplicationEx.operationsQueue.execute(destService);
	}

	/**
	 * On click listener
	 */
	private OnClickListener onClickListner = new OnClickListener() {

		@Override
		public void onClick(View view) {
			int id = view.getId();
			switch (id) {
			case R.id.submit:
				ApplicationEx.sourceLoc = sourceEditText.getText().toString();
				ApplicationEx.destinationLoc = destEditText.getText()
						.toString();
				if (validateUserLocation(ApplicationEx.sourceLoc,
						ApplicationEx.destinationLoc))
					getSourceLocation();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * Success callback for Destination Location service
	 */
	@Override
	public void onRetrieveDestRouteLocationFinished(Location location) {
		if (pd != null && pd.isShowing())
			pd.cancel();
		if (location == null) {
			destEditText.setError(getResources().getString(
					R.string.invalid_loc_msg));
		} else {
			ApplicationEx.destLocation = location;
			Intent intent = new Intent(HomeActivity.this,
					MapRouteActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	}

	/**
	 * Success callback for Source Location service
	 */
	@Override
	public void onRetrieveSrcRouteLocationFinished(Location location) {
		if (location == null) {
			if (pd != null && pd.isShowing())
				pd.cancel();
			sourceEditText.setError(getResources().getString(
					R.string.invalid_loc_msg));
		} else {
			ApplicationEx.srcLocation = location;
			getDestination();
		}
	}

	/**
	 * Failure callback for Destination Location service
	 */
	@Override
	public void onRetrieveDestRouteLocationFailed(int error, String message) {
		if (pd != null && pd.isShowing())
			pd.cancel();
		Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Failure callback for Source Location service
	 */
	@Override
	public void onRetrieveSrcRouteLocationFailed(int error, String message) {
		if (pd != null && pd.isShowing())
			pd.cancel();
		Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
	}

}
