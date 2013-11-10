package com.funontherun;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.SharedPreferences;

import com.funontherun.entities.Category;
import com.funontherun.entities.Location;
import com.funontherun.utils.FunUtils;

/**
 * Application level class to initialize and maintain various application life
 * cycle specific details
 */
public class ApplicationEx extends android.app.Application {
	/**
	 * used to set core number of threads
	 */
	private static final int CORE_POOL_SIZE = 3;
	public static boolean isNetworkAvailableFlag;
	/**
	 * used to set the maximum allowed number of threads.
	 */
	private static final int MAXIMUM_POOL_SIZE = 3;
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
	 * Locations for the user to select.
	 */
	public static final CharSequence[] locationItems = { "Along the Route",
			"Source Location", "Destination Location" };

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
