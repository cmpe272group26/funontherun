package com.funontherun.activities;

import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.TextView;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.View.OnClickListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.funontherun.ApplicationEx;
import com.funontherun.R;

public class ConcertDetailActivity extends FunBaseActivity {
	private ActionBar actionBarSherlock;
	private TextView titleTextView;
	private TextView artistTextView;
	private TextView venueTextView;
	private TextView addressTextView;
	private TextView startDateTextView;
	private TextView websiteTextView;
	private Button showMapButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.concert_details);

		actionBarSherlock = getSupportActionBar();

		actionBarSherlock.setTitle(getResources().getString(
				R.string.detail));
		actionBarSherlock.setHomeButtonEnabled(false);
		/**
		 * whether to show Standard Home Icon or not
		 */
		actionBarSherlock.setDisplayHomeAsUpEnabled(true);

		titleTextView = (TextView) findViewById(R.id.title_value);
		artistTextView = (TextView) findViewById(R.id.artist_value);
		venueTextView = (TextView) findViewById(R.id.venue_value);
		addressTextView = (TextView) findViewById(R.id.address_value);
		startDateTextView = (TextView) findViewById(R.id.start_date_value);
		websiteTextView = (TextView) findViewById(R.id.website_value);
		showMapButton = (Button) findViewById(R.id.show_map);

		if (ApplicationEx.concerts != null) {
			titleTextView.setText(ApplicationEx.concerts.getTitle());
			artistTextView.setText(ApplicationEx.concerts.getArtist());
			venueTextView.setText(ApplicationEx.concerts.getVenue());
			startDateTextView.setText(ApplicationEx.concerts.getStartDate());
			String website = ApplicationEx.concerts.getWebsite();
			if (TextUtils.isEmpty(website)) {
				websiteTextView.setText("N/A");
			} else {
				SpannableString content = new SpannableString(website);
				content.setSpan(new UnderlineSpan(), 0, website.length(), 0);
				websiteTextView.setText(content);
			}
			addressTextView.setText(ApplicationEx.concerts.getAddress());
			showMapButton.setOnClickListener(onClickListener);
			websiteTextView.setOnClickListener(onClickListener);
		}

	}

	/**
	 * Onclick listener
	 */
	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			Intent intent = null;
			int id = view.getId();
			switch (id) {
			case R.id.website_value:
				intent = new Intent(ConcertDetailActivity.this,
						WebViewActivity.class);
				intent.putExtra("url", ApplicationEx.concerts.getWebsite());
				startActivity(intent);
				break;
			case R.id.show_map:
				/**
				 * Display Driving directions from Current location to the user
				 * selected event location
				 */
				intent = new Intent(ConcertDetailActivity.this,
						PlotEventActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
			default:
				break;
			}

		}
	};

	/**
	 * Menu Item selection listener
	 */
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
