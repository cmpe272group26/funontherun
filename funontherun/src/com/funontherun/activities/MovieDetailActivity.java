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
import com.funontherun.entities.Movie;

public class MovieDetailActivity extends FunBaseActivity {
	private ActionBar actionBarSherlock;
	private TextView titleTextView;
	private TextView addressTextView;
	private TextView matchLevelTextView;
	private Button showMapButton;
	private Movie movie;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.movie_details);

		actionBarSherlock = getSupportActionBar();

		actionBarSherlock.setTitle(getResources().getString(
				R.string.detail));
		actionBarSherlock.setHomeButtonEnabled(false);
		/**
		 * whether to show Standard Home Icon or not
		 */
		actionBarSherlock.setDisplayHomeAsUpEnabled(true);

		titleTextView = (TextView) findViewById(R.id.title_value);
		addressTextView = (TextView) findViewById(R.id.shot_location_value);
		matchLevelTextView = (TextView) findViewById(R.id.match_level_value);
		showMapButton = (Button) findViewById(R.id.show_map);

		movie = getIntent().getParcelableExtra("movie");
		if (movie != null) {
			titleTextView.setText(movie.getName());
			matchLevelTextView.setText(movie.getMatchLevel());
			addressTextView.setText(movie.getAddress());
			ApplicationEx.movie.setLattitude(movie.getLattitude());
			ApplicationEx.movie.setLongitude(movie.getLongitude());
			showMapButton.setOnClickListener(onClickListener);
		}

	}

	/**
	 * OnClick Listener
	 */
	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			Intent intent = null;
			int id = view.getId();
			switch (id) {
			case R.id.show_map:
				/**
				 * Display Driving directions from Current location to the user
				 * selected event location
				 */
				intent = new Intent(MovieDetailActivity.this,
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
	 * Menu selection Listener
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
