package com.funontherun.services;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.funontherun.ApplicationEx;
import com.funontherun.entities.Movie;
import com.funontherun.services.utils.HTTPRequest;
import com.funontherun.utils.Constants;

/**
 * Service to retrieve Movies details those were shot along the route, at source
 * or at destination location.
 */
public class RetrieveMoviesService implements Runnable {
	/**
	 * Listener for MoviesService
	 */
	public interface RetrieveMoviesServiceListener {
		void onRetrieveMoviesFinished(ArrayList<Movie> movieList, String status);

		void onRetrieveMoviesFailed(int error, String message);
	}

	private static final String TAG = "RetrieveMoviesService";
	/** Retrieve Movies URL */
	private static String RETRIEVE_MOVIES_URL = "";
	private RetrieveMoviesServiceListener listener;
	private String jsonResponse;
	private int statusCode;
	private Context context;
	private String status;
	private ArrayList<Movie> movieList = new ArrayList<Movie>();

	public RetrieveMoviesService(Context context) {
		this.context = context;
	}

	/**
	 * Sends a GET request to retrieve Events
	 */
	public void run() {
		RETRIEVE_MOVIES_URL = Services.MOVIES_API_URL
				+ ApplicationEx.destLocation.getLattitude() + ","
				+ ApplicationEx.srcLocation.getLongitude()
				+ Services.MOVIE_DESTINATION
				+ ApplicationEx.destLocation.getLattitude() + ","
				+ ApplicationEx.destLocation.getLongitude();
		HTTPRequest request = new HTTPRequest(RETRIEVE_MOVIES_URL, context);
		Log.d("Movies Service", "URL::" + RETRIEVE_MOVIES_URL);

		Message message = new Message();
		try {
			statusCode = request.execute(HTTPRequest.RequestMethod.GET);
			jsonResponse = request.getResponseString();
			if (jsonResponse.contains("html"))
				message.what = Constants.FunOnTheRunDialogCodes.NETWORK_ERROR;
			message.what = statusCode;
			Log.d(TAG, "run::" + jsonResponse);
			movieHandler.sendMessage(message);
		} catch (Exception e) {
			message.what = statusCode;
			movieHandler.sendMessage(message);
			Log.e(TAG, "Movies Service exception::" + e);
		}

	}

	private Handler movieHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.FunOnTheRunDialogCodes.SUCCESS:
				if (!TextUtils.isEmpty(jsonResponse)) {
					movieList = parseRetrievedMovies(jsonResponse);
					listener.onRetrieveMoviesFinished(movieList, status);
				} else {
					listener.onRetrieveMoviesFailed(
							Constants.FunOnTheRunDialogCodes.NETWORK_ERROR,
							Constants.FunOnTheRunDialogMessages.NETWORK_ERROR);
				}
				break;
			case Constants.FunOnTheRunDialogCodes.DATA_NOT_FOUND:
				listener.onRetrieveMoviesFailed(
						Constants.FunOnTheRunDialogCodes.DATA_NOT_FOUND,
						Constants.FunOnTheRunDialogMessages.NOT_FOUND);
				break;
			case Constants.FunOnTheRunDialogCodes.INTERNAL_SERVER_ERROR:
				listener.onRetrieveMoviesFailed(
						Constants.FunOnTheRunDialogCodes.INTERNAL_SERVER_ERROR,
						Constants.FunOnTheRunDialogMessages.INTERNAL_SERVER_ERROR);
				break;
			case Constants.FunOnTheRunDialogCodes.NETWORK_ERROR:
				listener.onRetrieveMoviesFailed(
						Constants.FunOnTheRunDialogCodes.NETWORK_ERROR,
						Constants.FunOnTheRunDialogMessages.NETWORK_ERROR);
				break;
			default:
				listener.onRetrieveMoviesFailed(
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
	public RetrieveMoviesServiceListener getListener() {
		return listener;
	}

	/**
	 * Set listener
	 * 
	 * @return
	 */
	public void setListener(RetrieveMoviesServiceListener listener) {
		this.listener = listener;
	}

	private ArrayList<Movie> parseRetrievedMovies(String response) {

		try {
			JSONObject jsonObject = new JSONObject(jsonResponse);
			this.status = (jsonObject.has("status") ? jsonObject
					.getString("status") : "");
			JSONArray moviesArray = jsonObject.getJSONArray("results");
			for (int i = 0; i < moviesArray.length(); i++) {
				Movie movie = new Movie();
				JSONObject movieObject = moviesArray.getJSONObject(i);
				movie.deserializeJSON(movieObject);
				movieList.add(movie);
			}

			return movieList;
		} catch (JSONException e) {
			e.printStackTrace();
			listener.onRetrieveMoviesFailed(
					Constants.FunOnTheRunDialogCodes.DATA_NOT_FOUND,
					Constants.FunOnTheRunDialogMessages.NOT_FOUND);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return null;
	}

}
