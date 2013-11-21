package com.funontherun.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.Toast;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.funontherun.ApplicationEx;
import com.funontherun.R;
import com.funontherun.entities.Category;
import com.funontherun.entities.Concert;
import com.funontherun.entities.Movie;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class EventsOnMapActivity extends Activity implements
		OnInfoWindowClickListener, OnMarkerDragListener {
	/**
	 * Lat-long of Source Location
	 */
	private static LatLng SOURCE = null;
	/**
	 * Lat-long of Destination Location
	 */
	private static LatLng DESTINATION = null;

	/**
	 * List of Lat-long for plotting driving directions from source to
	 * destination
	 */
	public static List<LatLng> locationsList = new ArrayList<LatLng>();
	/**
	 * Marker for identifying source location on the Map.
	 */
	private Marker source;
	/**
	 * Marker for identifying Destination location on the Map.
	 */
	private Marker destination;
	/**
	 * For displaying Map
	 */
	private GoogleMap map;

	private ArrayList<LatLng> markerPoints;
	private ProgressDialog pd;

	private ActionBar actionBarSherlock;

	@Override
	protected void onCreate(Bundle inState) {
		super.onCreate(inState);

		SOURCE = new LatLng(ApplicationEx.srcLocation.getLattitude(),
				ApplicationEx.srcLocation.getLongitude());
		DESTINATION = new LatLng(ApplicationEx.destLocation.getLattitude(),
				ApplicationEx.destLocation.getLongitude());
		setContentView(R.layout.activity_map_demo);
		getLocations();
		actionBarSherlock = getSupportActionBar();
		actionBarSherlock.setHomeButtonEnabled(false);
		/**
		 * whether to show Standard Home Icon or not
		 */
		actionBarSherlock.setTitle(getResources().getString(
				R.string.categories_on_map));
		/**
		 * whether to show Standard Home Icon or not
		 */
		actionBarSherlock.setDisplayHomeAsUpEnabled(true);
		actionBarSherlock.setHomeButtonEnabled(true);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		markerPoints = new ArrayList<LatLng>();
		pd = ProgressDialog.show(this, "", "Loading Driving Directions...");

		/**
		 * Hide the zoom controls as the button panel will cover it.
		 */
		map.getUiSettings().setZoomControlsEnabled(false);

		/**
		 * Add lots of markers to the map.
		 */
		addMarkersToMap();
		// Create markers for the city data.
		// Must run this on the UI thread since it's a UI operation.
		// runOnUiThread(new Runnable() {
		// public void run() {
		try {
			addEventMarkersToMap();
		} catch (Exception e) {
			if (pd != null && pd.isShowing())
				pd.cancel();
			e.printStackTrace();
		}
		// }
		// });

		/**
		 * Setting an info window adapter allows us to change the both the
		 * contents and look of the info window.
		 */
		map.setInfoWindowAdapter(new CustomInfoWindowAdapter());

		/**
		 * Set listeners for marker events. See the bottom of this class for
		 * their behavior.
		 */
		map.setOnInfoWindowClickListener(this);

		/**
		 * Zoom in, animating the camera.
		 */
		map.setOnInfoWindowClickListener(this);
		map.setOnMarkerDragListener(this);
		// map.setOnMarkerClickListener(new OnMarkerClickListener() {
		//
		// @Override
		// public boolean onMarkerClick(final Marker marker) {
		// if (marker.equals(source)) {
		// bounceMarker(map, marker, SOURCE.longitude,
		// SOURCE.latitude, SOURCE);
		// } else if (marker.equals(destination)) {
		// bounceMarker(map, marker, DESTINATION.longitude,
		// DESTINATION.latitude, DESTINATION);
		// }
		// return false;
		// }
		// });

		/**
		 * Pan to see all markers in view. Cannot zoom to bounds until the map
		 * has a final size.
		 */
		final View mapView = getFragmentManager().findFragmentById(R.id.map)
				.getView();
		if (mapView.getViewTreeObserver().isAlive()) {
			mapView.getViewTreeObserver().addOnGlobalLayoutListener(
					new OnGlobalLayoutListener() {
						@SuppressWarnings("deprecation")
						/**
						 *  We use the new method when supported
						 */
						@SuppressLint("NewApi")
						/**
						 *  We check which build version we are using.
						 */
						@Override
						public void onGlobalLayout() {
							LatLngBounds bounds = new LatLngBounds.Builder()
									.include(SOURCE).include(DESTINATION)
									.build();
							if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
								mapView.getViewTreeObserver()
										.removeGlobalOnLayoutListener(this);
							} else {
								mapView.getViewTreeObserver()
										.removeOnGlobalLayoutListener(this);
							}
							map.moveCamera(CameraUpdateFactory.newLatLngBounds(
									bounds, 20));
							map.animateCamera(CameraUpdateFactory.zoomTo(8),
									3000, null);
						}
					});
		}

		/**
		 * Already two locations
		 */
		if (markerPoints.size() > 1) {
			markerPoints.clear();
			map.clear();
		}

		/**
		 * Adding new item to the ArrayList
		 */
		markerPoints.add(SOURCE);
		markerPoints.add(DESTINATION);

		/**
		 * Creating MarkerOptions
		 */
		MarkerOptions options = new MarkerOptions();

		/**
		 * Setting the position of the marker
		 */
		options.position(SOURCE);
		options.position(DESTINATION);

		/**
		 * For the start location, the color of marker is GREEN and for the end
		 * location, the color of marker is RED.
		 */
		if (markerPoints.size() == 1) {
			options.icon(BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
		} else if (markerPoints.size() == 2) {
			options.icon(BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_RED));
		}

		/**
		 * Checks, whether start and end locations are captured for generating
		 * route between them
		 */
		if (markerPoints.size() >= 2) {
			LatLng origin = markerPoints.get(0);
			LatLng dest = markerPoints.get(1);
		}

	}

	/**
	 * Bouncing Animation on the click of markers on the Google Map.
	 * 
	 * @param map
	 * @param marker
	 * @param longitude
	 * @param latitude
	 * @param latLng
	 */
	private void bounceMarker(GoogleMap map, final Marker marker,
			final double longitude, final double latitude, LatLng latLng) {
		/**
		 * This causes the marker at Perth to bounce into position when it is
		 * clicked.
		 */
		final Handler handler = new Handler();
		final long start = SystemClock.uptimeMillis();
		Projection proj = map.getProjection();
		Point startPoint = proj.toScreenLocation(latLng);
		startPoint.offset(0, -100);
		final LatLng startLatLng = proj.fromScreenLocation(startPoint);
		final long duration = 1500;

		final Interpolator interpolator = new BounceInterpolator();

		handler.post(new Runnable() {
			@Override
			public void run() {
				long elapsed = SystemClock.uptimeMillis() - start;
				float t = interpolator.getInterpolation((float) elapsed
						/ duration);
				double lng = t * longitude + (1 - t) * startLatLng.longitude;
				double lat = t * latitude + (1 - t) * startLatLng.latitude;
				marker.setPosition(new LatLng(lat, lng));

				if (t < 1.0) {
					/**
					 * Post again 16ms later.
					 */
					handler.postDelayed(this, 16);
				}
			}
		});
	}

	/**
	 * Display location message on click of the Info Window
	 */
	@Override
	public void onInfoWindowClick(Marker marker) {
		if (marker.getTitle().equalsIgnoreCase(ApplicationEx.sourceLoc))
			Toast.makeText(EventsOnMapActivity.this,
					"Your Source Location is " + marker.getTitle(),
					Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(EventsOnMapActivity.this,
					"Your Destination Location is " + marker.getTitle(),
					Toast.LENGTH_SHORT).show();
	}

	private void getLocations() {
		locationsList.add(SOURCE);
		locationsList.add(DESTINATION);

	}

	public void addEventMarkersToMap() {
		if (ApplicationEx.selectedLocation == 0) {
			if (ApplicationEx.key.equalsIgnoreCase("Restaurants")
					|| ApplicationEx.key.equalsIgnoreCase("Cafes")
					|| ApplicationEx.key.equalsIgnoreCase("ATM's")
					|| ApplicationEx.key.equalsIgnoreCase("Lodging")
					|| ApplicationEx.key.equalsIgnoreCase("Services")
					|| ApplicationEx.key.equalsIgnoreCase("Sightseeing")) {

				for (int i = 0; i < ApplicationEx.mainCategoryList.size(); i++) {
					for (int j = 0; j < ApplicationEx.mainCategoryList.get(i)
							.size(); j++) {
						Category category = ApplicationEx.mainCategoryList.get(
								i).get(j);
						map.addMarker(new MarkerOptions()
								.title(category.getName())
								.snippet(category.getAddress())
								.icon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
								.position(
										new LatLng(category.getLattitude(),
												category.getLongitude())));
					}
				}
			} else if (ApplicationEx.key.equalsIgnoreCase("Concerts")) {
				for (int i = 0; i < ApplicationEx.mainConcertList.size(); i++) {
					for (int j = 0; j < ApplicationEx.mainConcertList.get(i)
							.size(); j++) {
						Concert concert = ApplicationEx.mainConcertList.get(i)
								.get(j);
						map.addMarker(new MarkerOptions()
								.title(concert.getTitle())
								.snippet(concert.getAddress())
								.icon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
								.position(
										new LatLng(concert.getLattitude(),
												concert.getLongitude())));
					}
				}

			} else if (ApplicationEx.key.equalsIgnoreCase("Movie Locations")) {
				for (int i = 0; i < ApplicationEx.mainMovieList.size(); i++) {
					Movie movie = ApplicationEx.mainMovieList.get(i);
					map.addMarker(new MarkerOptions()
							.title(movie.getName())
							.snippet(movie.getAddress())
							.icon(BitmapDescriptorFactory
									.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
							.position(
									new LatLng(movie.getLattitude(), movie
											.getLongitude())));
				}
			}
		} else {
			if (ApplicationEx.key.equalsIgnoreCase("Restaurants")
					|| ApplicationEx.key.equalsIgnoreCase("Cafes")
					|| ApplicationEx.key.equalsIgnoreCase("ATM's")
					|| ApplicationEx.key.equalsIgnoreCase("Lodging")
					|| ApplicationEx.key.equalsIgnoreCase("Services")
					|| ApplicationEx.key.equalsIgnoreCase("Sightseeing")) {

				for (int i = 0; i < ApplicationEx.mapCategoryList.size(); i++) {
					Category category = ApplicationEx.mapCategoryList.get(i);
					map.addMarker(new MarkerOptions()
							.title(category.getName())
							.snippet(category.getAddress())
							.icon(BitmapDescriptorFactory
									.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
							.position(
									new LatLng(category.getLattitude(),
											category.getLongitude())));
				}
			} else if (ApplicationEx.key.equalsIgnoreCase("Concerts")) {
				for (int i = 0; i < ApplicationEx.mapConcertList.size(); i++) {
					Concert concert = ApplicationEx.mapConcertList.get(i);
					map.addMarker(new MarkerOptions()
							.title(concert.getTitle())
							.snippet(concert.getAddress())
							.icon(BitmapDescriptorFactory
									.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
							.position(
									new LatLng(concert.getLattitude(), concert
											.getLongitude())));
				}
			}
		}

		displayRoute();

	}

	public void displayRoute() {
		ArrayList<LatLng> points = null;
		PolylineOptions lineOptions = null;
		MarkerOptions markerOptions = new MarkerOptions();

		/**
		 * Traversing through all the routes
		 */
		for (int i = 0; i < ApplicationEx.result.size(); i++) {
			points = new ArrayList<LatLng>();
			lineOptions = new PolylineOptions();

			/**
			 * Fetching i-th route
			 */
			List<HashMap<String, String>> path = ApplicationEx.result.get(i);

			/**
			 * Fetching all the points in i-th route
			 */
			for (int j = 0; j < path.size(); j++) {
				HashMap<String, String> point = path.get(j);

				double lat = Double.parseDouble(point.get("lat"));
				double lng = Double.parseDouble(point.get("lng"));
				LatLng position = new LatLng(lat, lng);

				points.add(position);
				ApplicationEx.routePointsList.add(position);
			}

			/**
			 * Adding all the points in the route to LineOptions
			 */
			lineOptions.addAll(points);
			lineOptions.width(10);
			lineOptions.color(Color.RED);

			if (pd != null && pd.isShowing())
				pd.cancel();

		}

		if (lineOptions == null) {
			org.holoeverywhere.widget.Toast.makeText(EventsOnMapActivity.this,
					"Could not find directions...",
					org.holoeverywhere.widget.Toast.LENGTH_SHORT).show();
			if (pd != null && pd.isShowing())
				pd.cancel();
		} else {
			/**
			 * Drawing polyline in the Google Map for the i-th route
			 */
			map.addPolyline(lineOptions);
		}
	}

	private void addMarkersToMap() {
		/**
		 * Uses a colored icon.
		 */
		source = map.addMarker(new MarkerOptions().position(SOURCE).title(
				ApplicationEx.sourceLoc));

		/**
		 * Uses a custom icon.
		 */
		destination = map.addMarker(new MarkerOptions().position(DESTINATION)
				.title(ApplicationEx.destinationLoc));

	}

	/** Demonstrates customizing the info window and/or its contents. */
	class CustomInfoWindowAdapter implements InfoWindowAdapter {
		/**
		 * These a both viewgroups containing an ImageView with id "badge" and
		 * two TextViews with id" title" and "snippet".
		 */
		private final View mWindow;

		CustomInfoWindowAdapter() {
			mWindow = getLayoutInflater().inflate(R.layout.custom_info_window,
					null);
		}

		@Override
		public View getInfoWindow(Marker marker) {
			render(marker, mWindow);
			return mWindow;
		}

		private void render(Marker marker, View view) {
			String title = marker.getTitle();
			String snippet = marker.getSnippet();
			TextView titleUi = ((TextView) view.findViewById(R.id.title));
			TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
			if (title != null) {
				/**
				 * Spannable string allows us to edit the formatting of the
				 * text.
				 */
				SpannableString titleText = new SpannableString(title);
				titleText.setSpan(new ForegroundColorSpan(Color.RED), 0,
						titleText.length(), 0);
				titleUi.setText(titleText);
			} else {
				titleUi.setText("");
			}
			if (snippet != null) {
				/**
				 * Spannable string allows us to edit the formatting of the
				 * text.
				 */
				SpannableString snippetText = new SpannableString(snippet);
				snippetText.setSpan(new ForegroundColorSpan(Color.BLACK), 0,
						snippetText.length(), 0);
				snippetUi.setText(snippetText);
			} else {
				snippetUi.setText("");
			}

		}

		@Override
		public View getInfoContents(Marker marker) {
			return null;
		}
	}

	@Override
	public void onMarkerDrag(Marker marker) {

	}

	@Override
	public void onMarkerDragEnd(Marker marker) {

	}

	@Override
	public void onMarkerDragStart(Marker marker) {

	}

	/**
	 * On Menu Click
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

	/**
	 * Handles back press
	 */
	@Override
	public void onBackPressed() {

		super.onBackPressed();
	}

}
