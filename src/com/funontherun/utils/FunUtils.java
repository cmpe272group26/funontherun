package com.funontherun.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.funontherun.entities.Location;

public class FunUtils {
	public static List<Location> list;

	/**
	 * 
	 * @param checkNull
	 * @return empty string if the value is null
	 */
	public static String checkForNull(String checkNull) {
		if (checkNull != null && !checkNull.equalsIgnoreCase("null"))
			return checkNull;
		else
			return "";
	}

	/**
	 * method to check for network availability. returns true for available and
	 * false for unavailable
	 */
	public static boolean isConnectionAvailable(Context context) {
		ConnectivityManager conn = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetwork = conn
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobileNetwork = conn
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (wifiNetwork != null && wifiNetwork.isAvailable() == true
				&& wifiNetwork.isConnectedOrConnecting() == true) {
			return true;
		} else if (mobileNetwork != null && mobileNetwork.isAvailable() == true
				&& mobileNetwork.isConnectedOrConnecting() == true) {
			return true;
		} else
			return false;
	}

	/**
	 * Shows the status in Dialog.
	 * 
	 * @param context
	 * @param message
	 */
	public static void showStatus(Activity activity, String message,
			String title) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
		dialog.setCancelable(false);
		dialog.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				});
		dialog.show();
	}

	/**
	 * Category List
	 */
	public static List<String> getList() {
		List<String> categoryList = new ArrayList<String>();
		categoryList.add("Restaurants");
		categoryList.add("Museums");
		categoryList.add("Weather");
		categoryList.add("Tweets");
		categoryList.add("ATM's");
		categoryList.add("Universities");
		categoryList.add("Stadiums");

		return categoryList;

	}

	/**
	 * 
	 * @param key
	 * @return event values for the web service call
	 */
	public static String getEventValue(String key) {
		String value = "";

		if (key.equalsIgnoreCase("Restaurants"))
			value = "restaurant";
		else if (key.equalsIgnoreCase("Museums"))
			value = "museumt";
		else if (key.equalsIgnoreCase("Universities"))
			value = "university";
		else if (key.equalsIgnoreCase("ATM's"))
			value = "atm";
		else if (key.equalsIgnoreCase("Stadiums"))
			value = "stadium";

		return value;
	}

	/**
	 * 
	 * @param flag
	 * @return true if the object is open
	 */
	public static String isOpen(boolean flag) {
		if (flag)
			return "Open";
		else
			return "Closed";
	}

}
