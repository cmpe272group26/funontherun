package com.funontherun.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.funontherun.entities.Location;
import com.funontherun.services.utils.HTTPRequest;
import com.funontherun.utils.Constants;

/**
 * Service to retrieve Route Location list
 */
public class RetrieveDestRouteLocationService implements Runnable {
	/**
	 * Listener for RetrieveRouteLocationServiceListener
	 */
	public interface RetrieveDestRouteLocationServiceListener {
		void onRetrieveDestRouteLocationFinished(Location location);

		void onRetrieveDestRouteLocationFailed(int error, String message);
	}

	private static final String TAG = "RetrieveDestRouteLocationService";
	/** Route Location URL */
	private static String RETRIEVE_ROUTE_LOCATION_URL = "";
	private RetrieveDestRouteLocationServiceListener listener;
	private String jsonResponse;
	private int statusCode;
	private String searchQuery = "";
	private Context context;
	private Location destLocation = new Location();

	public RetrieveDestRouteLocationService(Context context, String searchQuery) {
		this.context = context;
		this.searchQuery = searchQuery;
	}

	/**
	 * Sends a GET request to retrieve Destination Location
	 */
	public void run() {
		if (searchQuery.contains(" "))
			searchQuery = searchQuery.replace(" ", "+");
		RETRIEVE_ROUTE_LOCATION_URL = Services.API_URL + searchQuery
				+ Services.SENSOR;
		HTTPRequest request = new HTTPRequest(RETRIEVE_ROUTE_LOCATION_URL,
				context);
		Log.d("Route Location Service", "URL::" + RETRIEVE_ROUTE_LOCATION_URL);

		Message message = new Message();
		try {
			statusCode = request.execute(HTTPRequest.RequestMethod.GET);
			jsonResponse = request.getResponseString();
			Log.d(TAG, "run::" + jsonResponse);
			message.what = statusCode;
			destinationLocationHandler.sendMessage(message);
		} catch (Exception e) {
			message.what = statusCode;
			destinationLocationHandler.sendMessage(message);
			Log.e(TAG, "destinationLocation Service exception::" + e);
		}

	}

	private Handler destinationLocationHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.FunOnTheRunDialogCodes.SUCCESS:
				if (!TextUtils.isEmpty(jsonResponse)) {
					destLocation = parseRetrievedDestLocation(jsonResponse);
					listener.onRetrieveDestRouteLocationFinished(destLocation);
				} else {
					listener.onRetrieveDestRouteLocationFailed(
							Constants.FunOnTheRunDialogCodes.NETWORK_ERROR,
							Constants.FunOnTheRunDialogMessages.NETWORK_ERROR);
				}
				break;
			case Constants.FunOnTheRunDialogCodes.DATA_NOT_FOUND:
				listener.onRetrieveDestRouteLocationFailed(
						Constants.FunOnTheRunDialogCodes.DATA_NOT_FOUND,
						Constants.FunOnTheRunDialogMessages.NOT_FOUND);
				break;
			case Constants.FunOnTheRunDialogCodes.INTERNAL_SERVER_ERROR:
				listener.onRetrieveDestRouteLocationFailed(
						Constants.FunOnTheRunDialogCodes.INTERNAL_SERVER_ERROR,
						Constants.FunOnTheRunDialogMessages.INTERNAL_SERVER_ERROR);
				break;
			case Constants.FunOnTheRunDialogCodes.NETWORK_ERROR:
				listener.onRetrieveDestRouteLocationFailed(
						Constants.FunOnTheRunDialogCodes.NETWORK_ERROR,
						Constants.FunOnTheRunDialogMessages.NETWORK_ERROR);
				break;
			default:
				listener.onRetrieveDestRouteLocationFailed(
						Constants.FunOnTheRunDialogCodes.NETWORK_ERROR,
						Constants.FunOnTheRunDialogMessages.NETWORK_ERROR);
				break;
			}
		}
	};

	/**
	 * Get listener
	 * 
	 * @return
	 */
	public RetrieveDestRouteLocationServiceListener getListener() {
		return listener;
	}

	/**
	 * Set listener
	 * 
	 * @return
	 */
	public void setListener(RetrieveDestRouteLocationServiceListener listener) {
		this.listener = listener;
	}

	/**
	 * Parse the response to get the destination location
	 * 
	 * @param response
	 * @return
	 */
	private Location parseRetrievedDestLocation(String response) {

		try {

			JSONObject jsonObject = new JSONObject(jsonResponse);
			JSONArray srcRouteArray = jsonObject.getJSONArray("results");
			Location location = new Location();
			JSONObject locationObject = srcRouteArray.getJSONObject(0);
			JSONObject geometryObject = locationObject
					.getJSONObject("geometry");
			location.deserializeJSON(geometryObject.getJSONObject("location"));

			return location;
		} catch (JSONException e) {
			e.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return null;
	}

}
