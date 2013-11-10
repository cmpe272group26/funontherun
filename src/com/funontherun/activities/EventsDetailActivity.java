package com.funontherun.activities;

import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.TextView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.funontherun.ApplicationEx;
import com.funontherun.R;
import com.funontherun.utils.FunUtils;

public class EventsDetailActivity extends FunBaseActivity {
	private ActionBar actionBarSherlock;
	private TextView nameTextView;
	private TextView addressTextView;
	private TextView ratingTextView;
	private TextView statusTextView;
	private Button showMapButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_details);

		actionBarSherlock = getSupportActionBar();

		actionBarSherlock.setTitle(getResources().getString(R.string.detail));
		actionBarSherlock.setHomeButtonEnabled(false);
		/**
		 * whether to show Standard Home Icon or not
		 */
		actionBarSherlock.setDisplayHomeAsUpEnabled(true);
		actionBarSherlock.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.nav_bar));

		nameTextView = (TextView) findViewById(R.id.name_value);
		addressTextView = (TextView) findViewById(R.id.address_value);
		ratingTextView = (TextView) findViewById(R.id.rating_value);
		statusTextView = (TextView) findViewById(R.id.status_value);
		showMapButton = (Button) findViewById(R.id.show_map);

		if (ApplicationEx.category != null) {
			nameTextView.setText(ApplicationEx.category.getName());
			addressTextView.setText(ApplicationEx.category.getAddress());
			ratingTextView.setText("" + ApplicationEx.category.getRating());
			statusTextView.setText(FunUtils.isOpen(ApplicationEx.category
					.isOpen()));
			showMapButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					/**
					 * Display Driving directions from Current location to the
					 * user selected event location
					 */
					Intent intent = new Intent(EventsDetailActivity.this,
							PlotEventActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			});
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
