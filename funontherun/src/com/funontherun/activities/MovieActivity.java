package com.funontherun.activities;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.Toast;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.funontherun.ApplicationEx;
import com.funontherun.R;
import com.funontherun.adapters.MoviesListAdapter;
import com.funontherun.entities.Movie;
import com.funontherun.services.RetrieveMoviesService;
import com.funontherun.services.RetrieveMoviesService.RetrieveMoviesServiceListener;
import com.funontherun.utils.FunUtils;

public class MovieActivity extends FunBaseActivity implements
		RetrieveMoviesServiceListener {
	private ActionBar actionBarSherlock;
	/**
	 * List View to display Movies shot based on user selected Location (Along
	 * the route, source or destination location)
	 */
	private ListView movieListView;
	private ProgressDialog pd;
	private MoviesListAdapter movieListAdapter;
	private List<Movie> movieList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_list);

		FunUtils.resetValues();
		actionBarSherlock = getSupportActionBar();

		actionBarSherlock.setHomeButtonEnabled(false);
		/**
		 * whether to show Standard Home Icon or not
		 */
		actionBarSherlock.setDisplayHomeAsUpEnabled(true);

		actionBarSherlock.setTitle(getResources().getString(
				R.string.movie_locations));

		movieList = new ArrayList<Movie>();
		movieListView = (ListView) findViewById(R.id.category_list_view);
		movieListAdapter = new MoviesListAdapter(movieList, this);
		movieListView.setAdapter(movieListAdapter);
		movieListView.setOnItemClickListener(onItemClickListener);
		getMovieLocationsAlongRoute();

	}

	/**
	 * Web service call for Movie Locations along the route.
	 */
	public void getMovieLocationsAlongRoute() {
		pd = ProgressDialog.show(MovieActivity.this, "", "Loading...", true);
		RetrieveMoviesService service = new RetrieveMoviesService(
				getApplicationContext());
		service.setListener(this);
		ApplicationEx.operationsQueue.execute(service);
	}

	/**
	 * Action for selecting menu items
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			finish();
			break;
		case R.id.map:
			Intent intent = new Intent(MovieActivity.this,
					EventsOnMapActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.movie_location_menu, menu);
		return true;
	}

	/**
	 * List view item click listener for the location details
	 */
	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Movie movie = (Movie) view.getTag(R.id.fun_id);
			Intent intent = new Intent(MovieActivity.this,
					MovieDetailActivity.class);
			intent.putExtra("movie", movie);
			ApplicationEx.movie = movie;
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	};

	public void showMessage() {
		Toast.makeText(getApplicationContext(),
				"Please select a valid option...", Toast.LENGTH_SHORT).show();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.funontherun.services.RetrieveMoviesService.RetrieveMoviesServiceListener
	 * #onRetrieveMoviesFinished(java.util.ArrayList)
	 */
	@Override
	public void onRetrieveMoviesFinished(ArrayList<Movie> resultList,
			String status) {
		ApplicationEx.mainMovieList = resultList;
		if (pd.isShowing() && pd != null)
			pd.cancel();
		if (resultList == null || resultList.size() == 0) {
			Toast.makeText(MovieActivity.this, "No results found...",
					Toast.LENGTH_SHORT).show();
		} else {
			movieListAdapter.setMovieList(resultList);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.funontherun.services.RetrieveMoviesService.RetrieveMoviesServiceListener
	 * #onRetrieveMoviesFailed(int, java.lang.String)
	 */
	@Override
	public void onRetrieveMoviesFailed(int error, String message) {
		if (pd.isShowing() && pd != null)
			pd.cancel();
		Toast.makeText(MovieActivity.this, "No results found...",
				Toast.LENGTH_SHORT).show();

	}

}
