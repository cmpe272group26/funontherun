package com.funontherun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.SharedPreferences;

import com.funontherun.entities.Category;
import com.funontherun.entities.Concert;
import com.funontherun.entities.Location;
import com.funontherun.entities.Movie;
import com.funontherun.utils.FunUtils;
import com.google.android.gms.maps.model.LatLng;

/**
 * Application level class to initialize and maintain various application life
 * cycle specific details
 */
public class ApplicationEx extends android.app.Application {
	/**
	 * used to set core number of threads
	 */
	private static final int CORE_POOL_SIZE = 6;
	public static boolean isNetworkAvailableFlag;
	/**
	 * used to set the maximum allowed number of threads.
	 */
	private static final int MAXIMUM_POOL_SIZE = 6;
	/**
	 * executes each submitted task using one of possibly several pooled threads
	 */
	public static ThreadPoolExecutor operationsQueue;
	/** Shared Preference to store login credentials. */
	public static SharedPreferences sharedPreference;
	/** Application Context */
	public static Context context;

	/**
	 * Source location of the user (Lat, Long)
	 */
	public static Location srcLocation = new Location();
	/**
	 * Destination location of the user (Lat, Long)
	 */
	public static Location destLocation = new Location();

	/**
	 * Current location of the user (Lat, Long)
	 */
	public static Location currentLocation = new Location();

	/**
	 * Source location of user in the form of string
	 */
	public static String sourceLoc = "";
	/**
	 * Destination location of user in the form of string
	 */
	public static String destinationLoc = "";

	/**
	 * Different Categories available to the user while traveling.
	 */
	public static Category category = new Category();
	/**
	 * Different Concerts available to the user while traveling.
	 */
	public static Concert concerts = new Concert();
	/**
	 * Different Movies that were shot on different locations while traveling.
	 */
	public static Movie movie = new Movie();

	/**
	 * Locations for the user to select.
	 */
	public static final CharSequence[] locationItems = { "Along the route",
			"Near source", "Near destination" };

	/**
	 * Range for the user in terms of miles.
	 */
	public static final CharSequence[] rangeItems = { "5", "10", "15", "20" };

	/**
	 * user selected location from the drop down
	 */
	public static int selectedLocation = 0;
	/**
	 * User selected Range from the drop down
	 */
	public static int selectedRange = 0;

	/**
	 * Range in terms of meters
	 */
	public static long rangeInMeters = 0;

	/**
	 * Location selected by user from the drop down for web service call
	 */
	public static Location userSelectedLocation;

	/**
	 * String that identifies user selected category
	 */
	public static String key;

	/**
	 * ArrayList containg all the intermediate points along the route
	 */
	public static List<LatLng> routePointsList = new ArrayList<LatLng>();

	/**
	 * counter to increment while calling simultaneous web services.
	 */
	public static int increment = 0;

	/**
	 * Main ArrayList of ArrayList of category containing results of multiple
	 * simultaneous web service calls
	 */
	public static ArrayList<ArrayList<Category>> mainCategoryList;

	/**
	 * Main ArrayList of ArrayList of Concerts containing results of multiple
	 * simultaneous web service calls
	 */
	public static ArrayList<ArrayList<Concert>> mainConcertList;

	/**
	 * Main ArrayList of ArrayList of Movies Locations containing results of
	 * multiple simultaneous web service calls
	 */
	public static ArrayList<Movie> mainMovieList;

	public static List<List<HashMap<String, String>>> result;

	public static ArrayList<Concert> mapConcertList;

	public static ArrayList<Category> mapCategoryList;

	public static int count = 0;

	/**
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		/**
		 * Check if the Network is available before making any network call.
		 */
		isNetworkAvailableFlag = FunUtils.isConnectionAvailable(context);
		operationsQueue = new ThreadPoolExecutor(CORE_POOL_SIZE,
				MAXIMUM_POOL_SIZE, 100000L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());

	}

}
