package com.funontherun.services;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.funontherun.entities.Category;
import com.funontherun.entities.Location;
import com.funontherun.services.utils.HTTPRequest;
import com.funontherun.utils.Constants;

/**
 * Service to retrieve events list near a particular location.
 */
public class RetrieveEventsService implements Runnable {
	/**
	 * Listener for RetrieveEventsService
	 */
	public interface RetrieveEventsServiceListener {
		void onRetrieveEventsFinished(List<Category> resultList);

		void onRetrieveEventsFailed(int error, String message);
	}

	private static final String TAG = "RetrieveSrcRouteLocationService";
	/** Route Location URL */
	private static String RETRIEVE_EVENTS_URL = "";
	private RetrieveEventsServiceListener listener;
	private String jsonResponse;
	private int statusCode;
	private String searchQuery = "";
	private Context context;
	private Location location;
	private long range;
	private List<Category> resultList = new ArrayList<Category>();

	public RetrieveEventsService(Context context, String searchQuery,
			Location location, long range) {
		this.context = context;
		this.searchQuery = searchQuery;
		this.location = location;
		this.range = range;
	}

	/**
	 * Sends a GET request to retrieve Events
	 */
	public void run() {
		if (searchQuery.contains(" "))
			searchQuery = searchQuery.replace(" ", "+");
		RETRIEVE_EVENTS_URL = Services.EVENTS_API_URL + location.getLattitude()
				+ "," + location.getLongitude() + Services.RADIUS + range
				+ Services.TYPES + searchQuery + Services.SENSOR + Services.KEY;
		HTTPRequest request = new HTTPRequest(RETRIEVE_EVENTS_URL, context);
		Log.d("Events Service", "URL::" + RETRIEVE_EVENTS_URL);

		Message message = new Message();
		try {
			statusCode = request.execute(HTTPRequest.RequestMethod.GET);
			message.what = statusCode;
			jsonResponse = request.getResponseString();
			Log.d(TAG, "run::" + jsonResponse);
			eventHandler.sendMessage(message);
		} catch (Exception e) {
			message.what = statusCode;
			eventHandler.sendMessage(message);
			Log.e(TAG, "Event Service exception::" + e);
		}

	}

	private Handler eventHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.FunOnTheRunDialogCodes.SUCCESS:
				if (!TextUtils.isEmpty(jsonResponse)) {
					resultList = parseRetrievedSrcLocation(jsonResponse);
					listener.onRetrieveEventsFinished(resultList);
				} else {
					listener.onRetrieveEventsFailed(
							Constants.FunOnTheRunDialogCodes.NETWORK_ERROR,
							Constants.FunOnTheRunDialogMessages.NETWORK_ERROR);
				}
				break;
			case Constants.FunOnTheRunDialogCodes.DATA_NOT_FOUND:
				listener.onRetrieveEventsFailed(
						Constants.FunOnTheRunDialogCodes.DATA_NOT_FOUND,
						Constants.FunOnTheRunDialogMessages.NOT_FOUND);
				break;
			case Constants.FunOnTheRunDialogCodes.INTERNAL_SERVER_ERROR:
				listener.onRetrieveEventsFailed(
						Constants.FunOnTheRunDialogCodes.INTERNAL_SERVER_ERROR,
						Constants.FunOnTheRunDialogMessages.INTERNAL_SERVER_ERROR);
				break;
			case Constants.FunOnTheRunDialogCodes.NETWORK_ERROR:
				listener.onRetrieveEventsFailed(
						Constants.FunOnTheRunDialogCodes.NETWORK_ERROR,
						Constants.FunOnTheRunDialogMessages.NETWORK_ERROR);
				break;
			default:
				listener.onRetrieveEventsFailed(
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
	public RetrieveEventsServiceListener getListener() {
		return listener;
	}

	/**
	 * Set listener
	 * 
	 * @return
	 */
	public void setListener(RetrieveEventsServiceListener listener) {
		this.listener = listener;
	}

	private List<Category> parseRetrievedSrcLocation(String response) {

		try {

			JSONObject jsonObject = new JSONObject(jsonResponse);
			JSONArray resultArray = jsonObject.getJSONArray("results");
			for (int i = 0; i < resultArray.length(); i++) {
				Category category = new Category();
				JSONObject categoryObject = resultArray.getJSONObject(i);
				category.deserializeJSON(categoryObject);
				resultList.add(category);
			}

			return resultList;
		} catch (JSONException e) {
			e.printStackTrace();
			listener.onRetrieveEventsFailed(
					Constants.FunOnTheRunDialogCodes.DATA_NOT_FOUND,
					Constants.FunOnTheRunDialogMessages.NOT_FOUND);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return null;
	}

}
