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
import com.funontherun.entities.Concert;
import com.funontherun.entities.Location;
import com.funontherun.entities.Weather;
import com.funontherun.services.utils.HTTPRequest;
import com.funontherun.utils.Constants;

/**
 * Service to retrieve concert details along the route or source, destination
 * location.
 */
public class RetrieveConcertsService implements Runnable {
	/**
	 * Listener for ConcertsService
	 */
	public interface RetrieveConcertsServiceListener {
		void onRetrieveConcertsFinished(ArrayList<Concert> concertList);

		void onRetrieveConcertsFailed(int error, String message);
	}

	private static final String TAG = "RetrieveConcertsService";
	/** Retrieve Concerts URL */
	private static String RETRIEVE_CONCERTS_URL = "";
	private RetrieveConcertsServiceListener listener;
	private String jsonResponse;
	private int statusCode;
	private Context context;
	private Location location;
	private int limit;
	private ArrayList<Concert> concertList = new ArrayList<Concert>();

	public RetrieveConcertsService(Context context, Location location, int limit) {
		this.context = context;
		this.location = location;
		this.limit = limit;
	}

	/**
	 * Sends a GET request to retrieve Events
	 */
	public void run() {
		RETRIEVE_CONCERTS_URL = Services.CONCERT_API_URL + Services.LAT
				+ location.getLattitude() + Services.LONG
				+ location.getLongitude() + Services.LIMIT + limit
				+ Services.CONCERT_API_KEY;
		HTTPRequest request = new HTTPRequest(RETRIEVE_CONCERTS_URL, context);
		Log.d("Concerts Service", "URL::" + RETRIEVE_CONCERTS_URL);

		Message message = new Message();
		try {
			statusCode = request.execute(HTTPRequest.RequestMethod.GET);
			jsonResponse = request.getResponseString();
			if (jsonResponse.contains("html"))
				message.what = Constants.FunOnTheRunDialogCodes.NETWORK_ERROR;
			message.what = statusCode;
			Log.d(TAG, "run::" + jsonResponse);
			concertHandler.sendMessage(message);
		} catch (Exception e) {
			message.what = statusCode;
			concertHandler.sendMessage(message);
			Log.e(TAG, "Concert Service exception::" + e);
		}

	}

	private Handler concertHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.FunOnTheRunDialogCodes.SUCCESS:
				if (!TextUtils.isEmpty(jsonResponse)) {
					concertList = parseRetrievedConcerts(jsonResponse);
					listener.onRetrieveConcertsFinished(concertList);
				} else {
					listener.onRetrieveConcertsFailed(
							Constants.FunOnTheRunDialogCodes.NETWORK_ERROR,
							Constants.FunOnTheRunDialogMessages.NETWORK_ERROR);
				}
				break;
			case Constants.FunOnTheRunDialogCodes.DATA_NOT_FOUND:
				listener.onRetrieveConcertsFailed(
						Constants.FunOnTheRunDialogCodes.DATA_NOT_FOUND,
						Constants.FunOnTheRunDialogMessages.NOT_FOUND);
				break;
			case Constants.FunOnTheRunDialogCodes.INTERNAL_SERVER_ERROR:
				listener.onRetrieveConcertsFailed(
						Constants.FunOnTheRunDialogCodes.INTERNAL_SERVER_ERROR,
						Constants.FunOnTheRunDialogMessages.INTERNAL_SERVER_ERROR);
				break;
			case Constants.FunOnTheRunDialogCodes.NETWORK_ERROR:
				listener.onRetrieveConcertsFailed(
						Constants.FunOnTheRunDialogCodes.NETWORK_ERROR,
						Constants.FunOnTheRunDialogMessages.NETWORK_ERROR);
				break;
			default:
				listener.onRetrieveConcertsFailed(
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
	public RetrieveConcertsServiceListener getListener() {
		return listener;
	}

	/**
	 * Set listener
	 * 
	 * @return
	 */
	public void setListener(RetrieveConcertsServiceListener listener) {
		this.listener = listener;
	}

	private ArrayList<Concert> parseRetrievedConcerts(String response) {

		try {

			JSONObject jsonObject = new JSONObject(jsonResponse);
			JSONObject eventJsonObject = jsonObject.getJSONObject("events");
			JSONArray resultArray = eventJsonObject.getJSONArray("event");
			for (int i = 0; i < resultArray.length(); i++) {
				Concert concert = new Concert();
				JSONObject concertObject = resultArray.getJSONObject(i);
				concert.deserializeJSON(concertObject);
				concertList.add(concert);
			}

			return concertList;
		} catch (JSONException e) {
			e.printStackTrace();
			listener.onRetrieveConcertsFailed(
					Constants.FunOnTheRunDialogCodes.DATA_NOT_FOUND,
					Constants.FunOnTheRunDialogMessages.NOT_FOUND);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return null;
	}

}
