package com.funontherun.activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.ProgressDialog;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.funontherun.ApplicationEx;
import com.funontherun.R;
import com.funontherun.services.DirectionsJSONParser;
import com.funontherun.utils.FunUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class PlotEventActivity extends FunBaseActivity implements
		OnInfoWindowClickListener, OnMarkerDragListener {

	public static List<LatLng> locationsList = new ArrayList<LatLng>();
	private Marker source;
	private Marker destination;
	private GoogleMap map;
	private ActionBar actionBarSherlock;
	private LatLng SOURCE = null;
	private LatLng DESTINATION = null;
	private ArrayList<LatLng> markerPoints;
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle inState) {
		super.onCreate(inState);
		setContentView(R.layout.activity_map_demo);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		actionBarSherlock = getSupportActionBar();

		actionBarSherlock.setTitle(getResources().getString(R.string.app_name));
		actionBarSherlock.setHomeButtonEnabled(false);
		/**
		 * whether to show Standard Home Icon or not
		 */
		actionBarSherlock.setDisplayHomeAsUpEnabled(true);

		try {

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				getActionBar().setDisplayHomeAsUpEnabled(true);
			}
			LocationManager locationManager;
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			Location location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);

			updateWithNewLocation(location);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public void plotEventsOnMap() {
		SOURCE = new LatLng(ApplicationEx.currentLocation.getLattitude(),
				ApplicationEx.currentLocation.getLongitude());
		if (ApplicationEx.key.equalsIgnoreCase("Concerts"))
			DESTINATION = new LatLng(ApplicationEx.concerts.getLattitude(),
					ApplicationEx.concerts.getLongitude());
		else if (ApplicationEx.key.equalsIgnoreCase("Movie Locations"))
			DESTINATION = new LatLng(ApplicationEx.movie.getLattitude(),
					ApplicationEx.movie.getLongitude());
		else
			DESTINATION = new LatLng(ApplicationEx.category.getLattitude(),
					ApplicationEx.category.getLongitude());

		markerPoints = new ArrayList<LatLng>();

		/**
		 * Hide the zoom controls as the button panel will cover it.
		 */
		map.getUiSettings().setZoomControlsEnabled(false);

		// Add lots of markers to the map.
		addMarkersToMap();

		// Setting an info window adapter allows us to change the both the
		// contents and look of the
		// info window.
		map.setInfoWindowAdapter(new CustomInfoWindowAdapter());

		// Set listeners for marker events. See the bottom of this class for
		// their behavior.
		map.setOnInfoWindowClickListener(this);

		// Move the camera instantly to hamburg with a zoom of 15.
		// map.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));

		// Zoom in, animating the camera.
		// map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
		map.setOnInfoWindowClickListener(this);
		map.setOnMarkerDragListener(this);
		map.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(final Marker marker) {
				if (marker.equals(source)) {
					bounceMarker(map, marker, SOURCE.longitude,
							SOURCE.latitude, SOURCE);
				} else if (marker.equals(destination)) {
					bounceMarker(map, marker, DESTINATION.longitude,
							DESTINATION.latitude, DESTINATION);
				}
				return false;
			}
		});

		// Pan to see all markers in view.
		// Cannot zoom to bounds until the map has a final size.
		final View mapView = getFragmentManager().findFragmentById(R.id.map)
				.getView();
		if (mapView.getViewTreeObserver().isAlive()) {
			mapView.getViewTreeObserver().addOnGlobalLayoutListener(
					new OnGlobalLayoutListener() {
						@SuppressWarnings("deprecation")
						// We use the new method when supported
						@SuppressLint("NewApi")
						// We check which build version we are using.
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
							map.animateCamera(CameraUpdateFactory.zoomTo(9),
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

			/**
			 * Getting URL to the Google Directions API
			 */
			String url = getDirectionsUrl(origin, dest);

			DownloadTask downloadTask = new DownloadTask();

			/**
			 * Start downloading json data from Google Directions API
			 */
			downloadTask.execute(url);
		}
	}

	private void bounceMarker(GoogleMap map, final Marker marker,
			final double longitude, final double latitude, LatLng latLng) {
		// This causes the marker at Perth to bounce into position when it is
		// clicked.
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
					// Post again 16ms later.
					handler.postDelayed(this, 16);
				}
			}
		});

	}

	/**
	 * 
	 * @param origin
	 * @param dest
	 * @return directions between source & destination locations
	 */
	private String getDirectionsUrl(LatLng origin, LatLng dest) {

		/**
		 * Origin of route
		 */
		String str_origin = "origin=" + origin.latitude + ","
				+ origin.longitude;

		/**
		 * Destination of route
		 */
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

		/**
		 * Sensor disabled
		 */
		String sensor = "sensor=false";

		/**
		 * Building the parameters to the web service
		 */
		String parameters = str_origin + "&" + str_dest + "&" + sensor;

		/**
		 * Output format
		 */
		String output = "json";

		/**
		 * Building the url to the web service
		 */
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + parameters;

		return url;
	}

	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			/**
			 * Creating an http connection to communicate with url
			 */
			urlConnection = (HttpURLConnection) url.openConnection();

			/**
			 * Connecting to url
			 */
			urlConnection.connect();

			/**
			 * Reading data from url
			 */
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	/**
	 * Fetches data from url passed
	 * 
	 * @author DEVEN
	 * 
	 */
	private class DownloadTask extends AsyncTask<String, Void, String> {

		/**
		 * Downloading data in non-ui thread
		 */
		@Override
		protected String doInBackground(String... url) {

			/**
			 * For storing data from web service
			 */
			String data = "";

			try {
				/**
				 * Fetching the data from web service
				 */
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		/**
		 * Executes in UI thread, after the execution of doInBackground()
		 */
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			ParserTask parserTask = new ParserTask();

			/**
			 * Invokes the thread for parsing the JSON data
			 */
			parserTask.execute(result);
		}
	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		/**
		 * Parsing the data in non-ui thread
		 */
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData) {

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser();

				/**
				 * Starts parsing data
				 */
				routes = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}

		/**
		 * Executes in UI thread, after the parsing process
		 */
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;
			MarkerOptions markerOptions = new MarkerOptions();

			/**
			 * Traversing through all the routes
			 */
			for (int i = 0; i < result.size(); i++) {
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();

				/**
				 * Fetching i-th route
				 */
				List<HashMap<String, String>> path = result.get(i);

				/**
				 * Fetching all the points in i-th route
				 */
				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);

					points.add(position);
				}

				if (pd != null && pd.isShowing())
					pd.cancel();

				/**
				 * Adding all the points in the route to LineOptions
				 */
				lineOptions.addAll(points);
				lineOptions.width(10);
				lineOptions.color(Color.RED);
			}

			if (lineOptions == null) {
				org.holoeverywhere.widget.Toast.makeText(
						PlotEventActivity.this, "Could not find directions...",
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
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		Toast.makeText(PlotEventActivity.this, marker.getTitle() + " Clicked",
				Toast.LENGTH_SHORT).show();
	}

	private void getLocations() {
		locationsList.add(SOURCE);
		locationsList.add(DESTINATION);

	}

	private void addMarkersToMap() {
		/**
		 * Uses a colored icon.
		 */
		source = map.addMarker(new MarkerOptions().position(SOURCE).title(
				"Current Location"));

		/**
		 * Uses a custom icon.
		 */
		destination = map.addMarker(new MarkerOptions().position(DESTINATION)
				.title("Destination Location"));
	}

	/** Demonstrates customizing the info window and/or its contents. */
	class CustomInfoWindowAdapter implements InfoWindowAdapter {
		/**
		 * These a both viewgroups containing an ImageView with id "badge" and
		 * two TextViews with id "title" and "snippet".
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
			TextView titleUi = ((TextView) view.findViewById(R.id.title));
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

		}

		@Override
		public View getInfoContents(Marker marker) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	@Override
	public void onMarkerDrag(Marker marker) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		// TODO Auto-generated method stub

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

	@Override
	public void onBackPressed() {

		super.onBackPressed();
	}

	/**
	 * Update Location with current location
	 * 
	 * @param location
	 */
	private void updateWithNewLocation(Location location) {
		if (location != null) {
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			ApplicationEx.currentLocation.setLattitude(lat);
			ApplicationEx.currentLocation.setLongitude(lng);
			pd = ProgressDialog.show(this, "", "Loading Driving Directions...");
			plotEventsOnMap();
			getLocations();

		} else {
			AlertDialog.Builder dialog = FunUtils.getDialogForStatus(
					PlotEventActivity.this, "Please turn on the GPS",
					"Enable GPS");
			dialog.setCancelable(false);
			dialog.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							finish();
						}
					});
			dialog.show();
		}
	}

}
